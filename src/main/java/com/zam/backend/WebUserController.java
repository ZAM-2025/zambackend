package com.zam.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class WebUserController {
    @Autowired
    private final ZamUserRepository repository;

    public static List<SessionToken> sessionTokens = new ArrayList<>();

    @GetMapping("/debugGetUser")
    public Iterable<ZamUser> debugGetUser() {
        return this.repository.findAll();
    }

    public static void WipeTokens() {
        sessionTokens.removeIf(SessionToken::isExpired);

        for(SessionToken sessionToken : sessionTokens) {
            System.out.println(sessionToken.toString());
        }
    }

    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserValidation auth(@RequestBody ZamAuthUser zamAuthUser) {
        WipeTokens();

        if(hasTokenForUser(zamAuthUser.username) == ZamTokenStatus.VALID) {
            SessionToken token = getTokenForUser(zamAuthUser.username);

            if(token != null && !token.isExpired()) {
                return new WebUserValidation(true, token.toString(), token.getCreationTimestamp());
            }
        }

        String user = zamAuthUser.username;
        String password = zamAuthUser.password;

        ZamUser dbUser = this.repository.findByUsername(user);

        if(dbUser == null) {
            ZamLogger.warning("Auth request for " + user + " failed: no such user");
            return new WebUserValidation(false, "No such user", LocalDateTime.now());
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] passHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String stringHash = Base64.getEncoder().encodeToString(passHash);

            if(stringHash.equals(dbUser.getPassword())) {
                SessionToken token = new SessionToken(dbUser);
                sessionTokens.add(token);

                ZamLogger.log("Auth request for " + user + " successful");
                return new WebUserValidation(true, token.toString(), LocalDateTime.now());
            } else {
                ZamLogger.warning("Auth request for " + user + " failed: wrong password");
                return new WebUserValidation(false, "Wrong password", LocalDateTime.now());
            }
        } catch(Exception ex) {
            return new WebUserValidation(false, "Server failure: " + ex.getMessage(), LocalDateTime.now());
        }
    }

    public static SessionToken getTokenForUser(String username) {
        for(SessionToken sessionToken : sessionTokens) {
            if(sessionToken.getUsername().equals(username)) {
                return sessionToken;
            }
        }

        return null;
    }

    public static ZamTokenStatus hasTokenForUser(String username) {
        for(SessionToken sessionToken : sessionTokens) {
            if(sessionToken.getUsername().equals(username)) {
                if(sessionToken.isExpired()) {
                    ZamLogger.warning("Token auth request failed: token expired");
                    return ZamTokenStatus.EXPIRED;
                } else {
                    ZamLogger.log("Token auth request successful");
                    return ZamTokenStatus.VALID;
                }
            }
        }

        return ZamTokenStatus.INVALID;
    }

    public static ZamTokenStatus validateToken(String token) {
        for(SessionToken sessionToken : sessionTokens) {
            if(sessionToken.toString().equals(token)) {
                if(sessionToken.isExpired()) {
                    ZamLogger.warning("Token auth request failed: token expired");
                    return ZamTokenStatus.EXPIRED;
                } else {
                    ZamLogger.log("Token auth request successful");
                    return ZamTokenStatus.VALID;
                }
            }
        }

        return ZamTokenStatus.INVALID;
    }

    @PostMapping(value = "/authToken", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserValidation authToken(@RequestBody ZamAuthToken zamAuthToken) {
        ZamTokenStatus valid = validateToken(zamAuthToken.token);
        WipeTokens();

        if(valid.equals(ZamTokenStatus.VALID)) {
            return new WebUserValidation(true, zamAuthToken.token, LocalDateTime.now());
        } else if (valid.equals(ZamTokenStatus.INVALID)) {
            return new WebUserValidation(false, "No such token", LocalDateTime.now());
        } else if (valid.equals(ZamTokenStatus.EXPIRED)) {
            return new WebUserValidation(false, "Token expired", LocalDateTime.now());
        }

        ZamLogger.warning("Token auth request failed: no such token");
        return new WebUserValidation(false, "No such token", LocalDateTime.now());
    }

    @GetMapping("/getCoordinatori")
    public Iterable<ZamUser> getCoordinatori() {
        return this.repository.findByTipo(ZamUserType.COORDINATORE);
    }

    @GetMapping("/getDipendenti")
    public Iterable<ZamUser> getDipendenti() {
        return this.repository.findByTipo(ZamUserType.DIPENDENTE);
    }

    @GetMapping("/getGestori")
    public Iterable<ZamUser> getGestori() {
        return this.repository.findByTipo(ZamUserType.GESTORE);
    }

    public WebUserController(ZamUserRepository repository) {
        this.repository = repository;
    }
}
