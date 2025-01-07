package com.zam.backend;

import java.time.LocalDateTime;

public record WebCaptchaRequest(boolean status, String id, String img, LocalDateTime timestamp) {}
