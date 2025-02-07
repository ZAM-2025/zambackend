package com.zam.backend;

import java.time.LocalDateTime;

public record WebUserValidation(boolean success, String message, LocalDateTime timestamp) {}
