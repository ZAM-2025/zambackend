package com.zam.backend;

import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
                return new WebBookingResponse(true, "OK");
            }
        }

        return new WebBookingResponse(false, "Prenotazione non trovata!");
    }

    // Ritorna true se è presente una prenotazione in conflitto, altrimenti ritorna false.
    private boolean checkDoubleBooking(Iterable<ZamBooking> bookings, ZonedDateTime start, ZonedDateTime end) {
        for (ZamBooking booking : bookings) {
            ZonedDateTime bookingStart = convertDate(booking.getInizio());
            ZonedDateTime bookingEnd = convertDate(booking.getFine());

            boolean startCondition = start.isBefore(bookingEnd) || bookingStart.isEqual(start);
            boolean endCondition = end.isAfter(bookingStart) || bookingStart.isEqual(start);

            if (startCondition && endCondition) {
                return true;
            }
        }

        return false;
    }

    private ZonedDateTime convertDate(LocalDateTime date) {
        return date.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Europe/Rome"));
    }

    private WebBookingResponse validateDates(ZonedDateTime start, ZonedDateTime end) {
        if(start.isAfter(end) || start.equals(end) || end.isBefore(start)) {
            ZamLogger.warning("Date condition 1");
            return new WebBookingResponse(false, "Date inserite non valide!");
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        if(start.isBefore(now) && !start.isEqual(now)) {
            ZamLogger.warning("Date condition 2 " + now);
            return new WebBookingResponse(false, "Date inserite non valide!");
        }

        return null;
    }

    @PostMapping(value = "/api/booking/book", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebBookingResponse book(@RequestBody WebBookingRequest request) {
        tokenRepository.clearTokens();

        ZonedDateTime start = convertDate(request.start());
        ZonedDateTime end = convertDate(request.end());

        WebBookingResponse validation = validateDates(start, end);
        if(validation != null) {
            return validation;
        }

        ZamToken token = tokenRepository.findZamTokenByVal(request.token());
        ZamUser user = this.tokenRepository.findUser(request.token());
        Optional<ZamAsset> asset = assetRepository.findById(request.asset());

        if(token == null || user == null || asset.isEmpty()) {
            return new WebBookingResponse(false, "Dati mancanti!");
        }

        Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset.get());

        switch(user.getTipo()) {
            case ZamUserType.GESTORE:
                {
                    return new WebBookingResponse(false, "I gestori non possono effettuare prenotazioni!");
                }
            case ZamUserType.DIPENDENTE:
                {
                    if(asset.get().getTipo() == ZamAssetType.C) {
                        return new WebBookingResponse(false, "Gli asset di tipo C non possono essere prenotati dai dipendenti!");
                    }
                }
                break;
            case ZamUserType.COORDINATORE:
                break;
        }

        if (checkDoubleBooking(bookings, start, end)) {
            return new WebBookingResponse(false, "L'asset è già prenotato nella fascia oraria!");
        }

        ZamBooking booking = new ZamBooking(user, asset.get(), start.toLocalDateTime(), end.toLocalDateTime());
        bookingRepository.save(booking);

        return new WebBookingResponse(true, "OK");
    }

    @PostMapping(value = "/api/booking/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebBookingResponse editBooking(@RequestBody WebBookingEditRequest request) {
        tokenRepository.clearTokens();

        ZonedDateTime start = convertDate(request.start());
        ZonedDateTime end = convertDate(request.end());

        WebBookingResponse validation = validateDates(start, end);
        if(validation != null) {
            return validation;
        }

        ZamToken token = tokenRepository.findZamTokenByVal(request.token());
        ZamUser user = this.tokenRepository.findUser(request.token());
        Optional<ZamBooking> booking = bookingRepository.findById(request.bookingID());

        if(token == null || user == null || booking.isEmpty()) {
            return new WebBookingResponse(false, "Dati mancanti!");
        }

        switch(user.getTipo()) {
            case ZamUserType.GESTORE:
                break;
            case ZamUserType.DIPENDENTE, ZamUserType.COORDINATORE:
            {
                if(user.getId() != booking.get().getIdUtente().getId()) {
                    return new WebBookingResponse(false, "Utente errato.");
                }

                if(booking.get().getNmod() >= 2) {
                    return new WebBookingResponse(false, "Numero massimo di modifiche superato!");
                }
            }
            break;
        }

        //Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(booking.get().getIdAsset());
        //if (checkDoubleBooking(bookings, start, end)) {
        //    return new WebBookingResponse(false, "L'asset è già prenotato nella fascia oraria!");
        //}

        // TODO: Gestire logica modifica (incremento nMod, cambio inizio e fine ecc.)
        ZamBooking newBooking = new ZamBooking();

        newBooking.setNmod(booking.get().getNmod() + 1);
        newBooking.setIdAsset(booking.get().getIdAsset());
        newBooking.setIdUtente(booking.get().getIdUtente());

        newBooking.setInizio(start.toLocalDateTime());
        newBooking.setFine(end.toLocalDateTime());

        bookingRepository.delete(booking.get());
        bookingRepository.save(newBooking);

        return new WebBookingResponse(true, "OK");
    }

    public WebBookingController(ZamBookingRepository bookingRepository, ZamAssetRepository assetRepository,
                                ZamUserRepository userRepository, ZamTokenRepository tokenRepository) {
        this.bookingRepository = bookingRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }
}
