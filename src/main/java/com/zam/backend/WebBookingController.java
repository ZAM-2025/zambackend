package com.zam.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class WebBookingController {
    private final ZamBookingRepository bookingRepository;
    private final ZamAssetRepository assetRepository;
    private final ZamUserRepository userRepository;
    private final ZamTokenRepository tokenRepository;

    @PostMapping(value = "/api/booking/asset", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<WebBookingAssetResponse> getByAsset(@RequestBody WebBookingAssetRequest request) {
        ZamAsset asset = assetRepository.findById(request.id()).get();
        Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset);

        List<WebBookingAssetResponse> res = new ArrayList<>();
        for (ZamBooking booking : bookings) {
            res.add(new WebBookingAssetResponse(booking, booking.getIdUtente().getId(), booking.isBooked()));
        }

        return res;
    }

    @PostMapping(value = "/api/booking/book", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebBookingResponse book(@RequestBody WebBookingRequest request) {
        tokenRepository.clearTokens();

        LocalDateTime start = request.start().plusHours(1);
        LocalDateTime end = request.end().plusHours(1);

        ZamToken token = tokenRepository.findZamTokenByVal(request.token());
        ZamUser user = this.tokenRepository.findUser(request.token());
        Optional<ZamAsset> asset = assetRepository.findById(request.asset());

        Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset.get());
        for (ZamBooking booking : bookings) {
            boolean startCondition = booking.getInizio().isAfter(start) || booking.getInizio().isEqual(start);
            boolean endCondition = booking.getFine().isBefore(end) || booking.getInizio().isEqual(start);

            ZamLogger.log(booking.getInizio());
            ZamLogger.log(request.start());
            ZamLogger.log(booking.getFine());
            ZamLogger.log(request.end());

            ZamLogger.log(startCondition);
            ZamLogger.log(endCondition);

            if (startCondition && endCondition) {
                return new WebBookingResponse(false);
            }
        }

        if(token == null || user == null || asset.isEmpty()) {
            return new WebBookingResponse(false);
        }

        ZamBooking booking = new ZamBooking(user, asset.get(), start, end);
        bookingRepository.save(booking);

        return new WebBookingResponse(true);
    }

    public WebBookingController(ZamBookingRepository bookingRepository, ZamAssetRepository assetRepository,
                                ZamUserRepository userRepository, ZamTokenRepository tokenRepository) {
        this.bookingRepository = bookingRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }
}
