package com.zam.backend;

public record WebNewUserRequest(String token, String username, String password,
                                String nome, String cognome, Integer type, Integer coord) { }
