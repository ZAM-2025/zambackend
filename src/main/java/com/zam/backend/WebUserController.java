package com.zam.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "*")
public class WebUserController {
    private final ZamUserRepository repository;
    Logger logger = LoggerFactory.getLogger(WebUserController.class);

    @GetMapping("/debugGetUser")
    public Iterable<ZamUser> debugGetUser() {
        Iterable<ZamUser> a = this.repository.findAll();
        logger.info(a.toString());
        return a;
    }

    public WebUserController(ZamUserRepository repository) {
        this.repository = repository;
    }
}
