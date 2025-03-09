package com.zam.backend;

import java.time.LocalDateTime;

public record WebBookingRequest(String token, Integer asset, LocalDateTime start, LocalDateTime end) { }
