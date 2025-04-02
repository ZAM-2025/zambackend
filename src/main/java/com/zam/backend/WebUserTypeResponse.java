package com.zam.backend;

public record WebUserTypeResponse(boolean success, String message, Iterable<WebUserInfo> list) { }
