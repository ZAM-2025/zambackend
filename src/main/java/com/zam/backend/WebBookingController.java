package com.zam.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class WebBookingController {
    private final ZamBookingRepository bookingRepository;
    private final ZamAssetRepository assetRepository;

    @PostMapping(value = "/api/booking/asset", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<WebBookingAssetResponse> getByAsset(@RequestBody WebBookingAssetRequest request) {
        ZamAsset asset = assetRepository.findById(request.id()).get();
        Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset);

        List<WebBookingAssetResponse> res = new ArrayList<>();
        for (ZamBooking booking : bookings) {
            res.add(new WebBookingAssetResponse(booking, booking.getIdUtente().getId()));
        }

        return res;
    }

    public WebBookingController(ZamBookingRepository bookingRepository, ZamAssetRepository assetRepository) {
        this.bookingRepository = bookingRepository;
        this.assetRepository = assetRepository;
    }
}
