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

    @PostMapping(value = "/api/booking/booked", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<WebBookingAssoc> getBooked(@RequestBody WebBookingRequest request) {
        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());

        List<WebBookingAssoc> out = new ArrayList<>();

        Iterable<ZamBooking> bookings = bookingRepository.findAll();
        for(ZamBooking booking : bookings) {
            if(booking.getIdUtente().getId() == user.getId()) {
                ZamAsset asset = booking.getIdAsset();
                out.add(new WebBookingAssoc(booking, asset.getNome(), asset.getPiano()));
            }
        }

        return out;
    }

    @PostMapping(value = "/api/booking/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebBookingResponse deleteBooking(@RequestBody WebBookingDeleteRequest request) {
        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());

        for(ZamBooking booking : bookingRepository.findAll()) {
            if(request.bookingID().equals(booking.getId()) && booking.getIdUtente().getId() == user.getId()) {
                bookingRepository.delete(booking);
                return new WebBookingResponse(true);
            }
        }

        return new WebBookingResponse(false);
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

            boolean startCondition = start.isBefore(booking.getFine()) || booking.getInizio().isEqual(start);
            boolean endCondition = end.isAfter(booking.getInizio()) || booking.getInizio().isEqual(start);

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
