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

@RestController
@CrossOrigin(origins = "*")
public class WebUserController {
    @Autowired
    private final ZamUserRepository userRepository;
    private final ZamTokenRepository tokenRepository;

    // TODO: Aggiungere creazione utente per coordinatori
    // TODO: Aggiungere validazione password

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

    @PostMapping(value = "/api/user/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserInfo getUserInfo(@RequestBody ZamAuthToken token) {
        ZamUser user = this.tokenRepository.findUser(token.token);

        if(user == null) {
            ZamLogger.warning("User not found for token " + token.token);
            return new WebUserInfo(false, null, null, null, null);
        }

        ZamLogger.log("User " + user.getUsername() + " found for token " + token.token);
        return new WebUserInfo(true, user.getUsername(), user.getNome(), user.getCognome(), user.getTipo());
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
