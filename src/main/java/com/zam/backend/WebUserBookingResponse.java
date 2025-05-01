package com.zam.backend;

import java.util.List;

public record WebUserBookingResponse(boolean success, String message, List<WebBookingAssoc> bookings) { }
