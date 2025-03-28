package com.zam.backend;

import java.time.LocalDateTime;

public record WebBookingEditRequest(String token, Integer bookingID, LocalDateTime start, LocalDateTime end) { }
