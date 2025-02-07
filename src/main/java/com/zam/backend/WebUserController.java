package com.zam.backend;

import com.zam.captcha.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;


@RestController
@CrossOrigin(origins = "*")
public class WebUserController {
    @Autowired
    private final ZamUserRepository repository;
    Logger logger = LoggerFactory.getLogger(WebUserController.class);

    private List<SessionToken> sessionTokens;

    @GetMapping("/debugGetUser")
    public Iterable<ZamUser> debugGetUser() {
        Iterable<ZamUser> a = this.repository.findAll();
        return a;
    }

    private void WipeTokens() {
        for(SessionToken sessionToken : this.sessionTokens) {
            if(sessionToken.isExpired()) {
                this.sessionTokens.remove(sessionToken);
            }
        }
    }

    @PostMapping("/login/{user}")
    public WebUserValidation login(@PathVariable("user") String user, @RequestBody String password) {
        this.WipeTokens();

        ZamUser dbUser = this.repository.findByUsername(user);

        if(dbUser == null) {
            return new WebUserValidation(false, "No such user", LocalDateTime.now());
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] passHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String stringHash = Base64.getEncoder().encodeToString(passHash);

            if(stringHash.equals(dbUser.getPassword())) {
                SessionToken token = new SessionToken(dbUser);
                sessionTokens.add(token);

                return new WebUserValidation(true, token.toString(), LocalDateTime.now());
            } else {
                return new WebUserValidation(false, "Wrong password", LocalDateTime.now());
            }
        } catch(Exception ex) {
            return new WebUserValidation(false, "Server failure: " + ex.getMessage(), LocalDateTime.now());
        }
    }

    @PostMapping("/loginToken/{user}")
    public WebUserValidation loginToken(@PathVariable("user") String user, @RequestBody String token) {
        for(SessionToken sessionToken : this.sessionTokens) {
            if(sessionToken.toString().equals(token)) {
                if(sessionToken.isExpired()) {
                    return new WebUserValidation(false, "Token expired", LocalDateTime.now());
                } else {
                    return new WebUserValidation(true, sessionToken.toString(), LocalDateTime.now());
                }
            }
        }

        this.WipeTokens();

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
