package com.zam.backend;

import java.time.LocalDateTime;

public record WebCaptchaRequest(String id, String img, LocalDateTime timestamp) {}
