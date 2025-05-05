package com.zam.backend;

import com.zam.captcha.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "*")
public class WebUserController {
    @Autowired
    private final ZamUserRepository userRepository;
    private final ZamTokenRepository tokenRepository;
    private final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\/-]).{8,}$";
    private final String PASSWORD_ERROR = "La password inserita non soddisfa i criteri!\n(Minimo 8 caratteri, maiuscole e minuscole, almeno un numero e un simbolo)";

    @PostMapping(value = "/api/user/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserValidation logout(@RequestBody ZamAuthToken token) {
        ZamToken t = this.tokenRepository.findZamTokenByVal(token.token);
        if(t == null) {
            ZamLogger.warning("Failed to logout");
            return new WebUserValidation(false, "Failed to logout", LocalDateTime.now());
        }

        ZamLogger.log("Logged out a user");
        this.tokenRepository.delete(t);
        return new WebUserValidation(true, "User logged out", LocalDateTime.now());
    }

    @PostMapping(value = "/api/user/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse deleteUser(@RequestBody WebDeleteUserRequest request) {
        tokenRepository.clearTokens();
        ZamUser user = this.tokenRepository.findUser(request.token());

        if(user == null) {
            return new WebGenericResponse(false, "No such user");
        }

        if(user.getTipo() != ZamUserType.GESTORE) {
            return new WebGenericResponse(false, "Not allowed");
        }

        Optional<ZamUser> deleteUser = this.userRepository.findById(request.userID());

        if(deleteUser.isEmpty()) {
            return new WebGenericResponse(false, "No such user");
        }

        this.userRepository.delete(deleteUser.get());
        return new WebGenericResponse(true, "OK");
    }

    private boolean passwordIsValid(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    @PostMapping(value = "/api/user/coord", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ZamUser> getCoordinatedUsers(@RequestBody ZamAuthToken request) {
        tokenRepository.clearTokens();
        ZamUser user = this.tokenRepository.findUser(request.token);

        if(user == null) {
            return new ArrayList<>();
        }

        if(user.getTipo() == ZamUserType.DIPENDENTE) {
            return new ArrayList<>();
        }

        List<ZamUser> users = new ArrayList<>();

        if(user.getTipo() == ZamUserType.COORDINATORE) {
            users = userRepository.findByCoordinatore(user);
            for(ZamUser u : users) {
                u.setPassword(null);
            }
        } else if(user.getTipo() == ZamUserType.GESTORE) {
            users = (List<ZamUser>) userRepository.findAll();
            for(ZamUser u : users) {
                u.setPassword(null);
            }
            users.removeIf(u -> u.getId() == user.getId());
        }

        return users;
    }

    @PostMapping(value = "/api/user/new", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse newUser(@RequestBody WebNewUserRequest request) {
        if(!passwordIsValid(request.password())) {
            return new WebGenericResponse(false, PASSWORD_ERROR);
        }

        tokenRepository.clearTokens();
        ZamUser user = this.tokenRepository.findUser(request.token());

        if(user == null) {
            return new WebGenericResponse(false, "No such user");
        }

        if(user.getTipo() != ZamUserType.GESTORE) {
            return new WebGenericResponse(false, "Not allowed");
        }

        if(request.type() < 0 || request.type() > ZamUserType.values().length) {
            return new WebGenericResponse(false, "Bad request");
        }

        ZamUserType type = ZamUserType.values()[request.type()];
        if(type == ZamUserType.GESTORE) {
            return new WebGenericResponse(false, "Bad request");
        }

        if(request.coord() == null && type != ZamUserType.COORDINATORE) {
            return new WebGenericResponse(false, "Bad request");
        }

        ZamUser coordinatore = null;

        if(type == ZamUserType.COORDINATORE) {
            int count = this.userRepository.countZamUsersByTipo(ZamUserType.COORDINATORE);

            if(count >= 2) {
                return new WebGenericResponse(false, "Limite di coordinatori raggiunto!");
            }
        } else {
            Optional<ZamUser> _coordinatore = userRepository.findById(request.coord());
            if(_coordinatore.isEmpty()) {
                return new WebGenericResponse(false, "No such user");
            } else {
                coordinatore = _coordinatore.get();
            }
        }

        // TODO: Aggiungere criteri password!
        ZamUser newUser = new ZamUser(request.username(), "",
                request.nome(), request.cognome(),
                type, coordinatore);

        try {
            newUser.setPassword(calculateHash(request.password()));
        } catch (NoSuchAlgorithmException e) {
            return new WebGenericResponse(false, "Server error");
        }

        userRepository.save(newUser);
        return new WebGenericResponse(true, "OK");
    }

    @PostMapping(value = "/api/user/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse editUser(@RequestBody WebEditUserRequest request) {
        if(!passwordIsValid(request.password())) {
            return new WebGenericResponse(false, PASSWORD_ERROR);
        }

        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());
        ZamUser editUser = this.userRepository.findById(request.id()).orElse(null);

        if(user == null || editUser == null) {
            return new WebGenericResponse(false, "No such user");
        }

        if(user.getTipo() != ZamUserType.GESTORE) {
            return new WebGenericResponse(false, "Not allowed");
        }

        if(request.type() < 0 || request.type() > ZamUserType.values().length) {
            return new WebGenericResponse(false, "Bad request");
        }

        ZamUserType type = ZamUserType.values()[request.type()];
        if(type == ZamUserType.GESTORE) {
            return new WebGenericResponse(false, "Bad request");
        }

        if(request.coord() == null && type != ZamUserType.COORDINATORE) {
            return new WebGenericResponse(false, "Bad request");
        }

        ZamUser coordinatore = null;

        if(type == ZamUserType.DIPENDENTE) {
            Optional<ZamUser> _coordinatore = userRepository.findById(request.coord());
            if(_coordinatore.isEmpty()) {
                return new WebGenericResponse(false, "No such user");
            } else {
                coordinatore = _coordinatore.get();
            }
        }

        // TODO: Aggiungere criteri password!
        editUser.setUsername(request.username());
        editUser.setNome(request.nome());
        editUser.setCognome(request.cognome());
        editUser.setCoordinatore(coordinatore);

        try {
            editUser.setPassword(calculateHash(request.password()));
        } catch (NoSuchAlgorithmException e) {
            return new WebGenericResponse(false, "Server error");
        }

        userRepository.save(editUser);
        return new WebGenericResponse(true, "OK");
    }

    @PostMapping(value = "/api/user/type", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserTypeResponse getUserInfoByType(@RequestBody WebUserTypeRequest request) {
        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());

        if(user == null) {
            return new WebUserTypeResponse(false, "User not found", Collections.emptyList());
        }

        if(user.getTipo() == ZamUserType.DIPENDENTE) {
            return new WebUserTypeResponse(false, "Not allowed", Collections.emptyList());
        }

        ZamUserType[] types = ZamUserType.values();
        boolean isValidType = false;

        for(ZamUserType type : types) {
            if(Objects.equals(request.type(), type.name())) {
                isValidType = true;
                break;
            }
        }

        if(!isValidType) {
            return new WebUserTypeResponse(false, "Bad request", Collections.emptyList());
        }

        ZamUserType type = ZamUserType.valueOf(request.type());
        Iterable<ZamUser> users = userRepository.findByTipo(type);

        List<WebUserInfo> list = new ArrayList<>();

        for(ZamUser u : users) {
            list.add(new WebUserInfo(u.getId(), true, u.getUsername(), u.getNome(), u.getCognome(), u.getTipo(), u.getCoordInfo()));
        }

        return new WebUserTypeResponse(true, "OK", list);
    }

    @PostMapping(value = "/api/user/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserInfo getUserInfo(@RequestBody ZamAuthToken token) {
        tokenRepository.clearTokens();
        ZamUser user = this.tokenRepository.findUser(token.token);

        if(user == null) {
            ZamLogger.warning("User not found for token " + token.token);
            return new WebUserInfo(-1, false, null, null, null, null, null);
        }

        ZamLogger.log("User " + user.getUsername() + " found for token " + token.token);
        return new WebUserInfo(user.getId(), true, user.getUsername(), user.getNome(), user.getCognome(), user.getTipo(), user.getCoordInfo());
    }

    @PostMapping(value = "/api/user/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserValidation auth(@RequestBody ZamAuthUser zamAuthUser) {
        tokenRepository.clearTokens();

        boolean hasCaptcha = false;
        for(Captcha c : WebCaptchaController.getValidatedCaptchas()) {
            if(Objects.equals(c.getId(), zamAuthUser.captchaID)) {
                ZamLogger.log("Captcha ID " + c.getId());

                WebCaptchaController.removeValidatedCaptcha(c.getId());
                hasCaptcha = true;
                break;
            }
        }

        if(!hasCaptcha) {
            return new WebUserValidation(false, "Captcha not sent", LocalDateTime.now());
        }

        String user = zamAuthUser.username;
        String password = zamAuthUser.password;
        ZamUser dbUser = this.userRepository.findByUsername(user);

        if(dbUser == null) {
            ZamLogger.warning("Auth request for " + user + " failed: no such user");
            return new WebUserValidation(false, "No such user", LocalDateTime.now());
        }

        try {
            ZamToken token = tokenRepository.findZamTokenByIdutente(dbUser).getFirst();
            return matchTokenHash(password, dbUser, token.toString());
        } catch (NoSuchElementException ex) {
            ZamLogger.warning("Auth request for " + user + " failed: no such user");
        }

        return matchHash(password, dbUser);
    }

    private WebUserValidation matchTokenHash(String password, ZamUser dbUser, String token) {
        try {
            String stringHash = calculateHash(password);

            if(stringHash.equals(dbUser.getPassword())) {
                ZamLogger.log("Auth request for " + dbUser.getUsername() + " successful");
                return new WebUserValidation(true, token, LocalDateTime.now());
            } else {
                ZamLogger.warning("Auth request for " + dbUser.getUsername() + " failed: wrong password");
                return new WebUserValidation(false, "Wrong password", LocalDateTime.now());
            }
        } catch(Exception ex) {
            return new WebUserValidation(false, "Server failure: " + ex.getMessage(), LocalDateTime.now());
        }
    }

    private WebUserValidation matchHash(String password, ZamUser dbUser) {
        try {
            String stringHash = calculateHash(password);

            if(stringHash.equals(dbUser.getPassword())) {
                ZamToken token = new ZamToken(dbUser);
                tokenRepository.save(token);

                ZamLogger.log("Auth request for " + dbUser.getUsername() + " successful");
                return new WebUserValidation(true, token.toString(), LocalDateTime.now());
            } else {
                ZamLogger.warning("Auth request for " + dbUser.getUsername() + " failed: wrong password");
                return new WebUserValidation(false, "Wrong password", LocalDateTime.now());
            }
        } catch(Exception ex) {
            return new WebUserValidation(false, "Server failure: " + ex.getMessage(), LocalDateTime.now());
        }
    }

    private String calculateHash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] passHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(passHash);
    }

    @PostMapping(value = "/api/user/token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserValidation authToken(@RequestBody ZamAuthToken zamAuthToken) {
        tokenRepository.clearTokens();
        ZamToken token = tokenRepository.findZamTokenByVal(zamAuthToken.token);

        if(token != null) {
            return new WebUserValidation(true, zamAuthToken.token, LocalDateTime.now());
        } else {
            ZamLogger.warning("Token auth request failed: no such token");
            return new WebUserValidation(false, "No such token", LocalDateTime.now());
        }
    }

    public WebUserController(ZamUserRepository userRepository, ZamTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }
}
