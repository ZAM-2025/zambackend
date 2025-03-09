package com.zam.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

        ZamToken token = tokenRepository.findZamTokenByVal(request.token());
        ZamUser user = this.tokenRepository.findUser(request.token());
        Optional<ZamAsset> asset = assetRepository.findById(request.asset());

        Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset.get());
        for (ZamBooking booking : bookings) {
            boolean startCondition = booking.getInizio().isAfter(request.start()) || booking.getInizio().isEqual(request.start());
            boolean endCondition = booking.getFine().isBefore(request.end()) || booking.getFine().isEqual(request.end());

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

        ZamBooking booking = new ZamBooking(user, asset.get(), request.start(), request.end());
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
