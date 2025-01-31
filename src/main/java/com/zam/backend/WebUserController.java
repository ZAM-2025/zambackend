package com.zam.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "*")
public class WebUserController {
    @Autowired
    private final ZamUserRepository repository;
    Logger logger = LoggerFactory.getLogger(WebUserController.class);

    @GetMapping("/debugGetUser")
    public Iterable<ZamUser> debugGetUser() {
        Iterable<ZamUser> a = this.repository.findAll();
        return a;
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
