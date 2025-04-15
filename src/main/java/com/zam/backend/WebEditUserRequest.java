package com.zam.backend;

public record WebEditUserRequest(String token, int id, String username, String password,
                                 String nome, String cognome, Integer type, Integer coord) { }
