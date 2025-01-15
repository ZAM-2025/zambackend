package com.zam.backend;

import jakarta.persistence.*;

@Entity
@Table(name = "zamuser")
public class ZamUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;

    protected ZamUser() {}

    public ZamUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }
}
