package com.zam.backend;

public record WebBookingAssetResponse(ZamBooking body, Integer userId, boolean isBooked, String nome, String cognome) { }
