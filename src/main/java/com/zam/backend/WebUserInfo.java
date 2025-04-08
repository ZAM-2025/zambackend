package com.zam.backend;

public record WebUserInfo(int id, boolean success, String username, String nome, String cognome, ZamUserType type) { }
