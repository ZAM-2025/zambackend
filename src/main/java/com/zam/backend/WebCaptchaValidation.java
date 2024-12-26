package com.zam.backend;

import java.time.LocalDateTime;

public record WebCaptchaValidation(boolean success, String id, LocalDateTime timestamp, String message) {}
