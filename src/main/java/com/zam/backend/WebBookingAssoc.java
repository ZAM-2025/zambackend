package com.zam.backend;

public record WebBookingAssoc(ZamBooking booking, String assetName, Integer assetFloor, Integer assetID, boolean active) { }
