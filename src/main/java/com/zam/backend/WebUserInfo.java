package com.zam.backend;

public record WebUserInfo(boolean success, String username, String nome, String cognome, ZamUserType type) { }
