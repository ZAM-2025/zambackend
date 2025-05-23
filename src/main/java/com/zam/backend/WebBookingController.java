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

    private List<ZamBooking> getNumActiveForUser(ZamUser user) {
        List<ZamBooking> bookings = bookingRepository.findByIdUtente(user);

        bookings.removeIf(b -> !b.isBooked());
        for(ZamBooking booking : bookings) {
            ZamLogger.log(booking.getIdAsset().getNome() + " " + booking.isBooked());
        }

        return bookings;
    }

    private boolean bookingsHaveAsset(List<ZamBooking> active, ZamAsset asset) {
        for(ZamBooking booking : active) {
            if(booking.getIdAsset().getId().equals(asset.getId())) {
                return true;
            }
        }

        return false;
    }

    @PostMapping(value = "/api/booking/asset", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<WebBookingAssetResponse> getByAsset(@RequestBody WebBookingAssetRequest request) {
        ZamAsset asset = assetRepository.findById(request.id()).get();
        Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset);

        List<WebBookingAssetResponse> res = new ArrayList<>();
        for (ZamBooking booking : bookings) {
            res.add(new WebBookingAssetResponse(booking, booking.getIdUtente().getId(), booking.isBooked(), booking.getIdUtente().getNome(), booking.getIdUtente().getCognome()));
        }

        return res;
    }

    @PostMapping(value = "/api/booking/assetfor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<WebBookingAssetResponse> getByAssetCoord(@RequestBody WebBookingUserAssetRequest request) {
        ZamUser user = tokenRepository.findUser(request.token());
        if(user == null || user.getTipo() == ZamUserType.DIPENDENTE) {
            return new ArrayList<>();
        }

        ZamAsset asset = assetRepository.findById(request.assetID()).get();
        List<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset);

        bookings.removeIf(b -> b.getFine().isBefore(LocalDateTime.now()));

        List<WebBookingAssetResponse> res = new ArrayList<>();
        for (ZamBooking booking : bookings) {
            if(user.getTipo() == ZamUserType.GESTORE ||
                (booking.getIdUtente().getCoordinatore() != null
                && booking.getIdUtente().getCoordinatore().getId() == user.getId())
            ) {
                res.add(new WebBookingAssetResponse(booking, booking.getIdUtente().getId(), booking.isBooked(), booking.getIdUtente().getNome(), booking.getIdUtente().getCognome()));
            }
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
                out.add(new WebBookingAssoc(booking, asset.getNome(), asset.getPiano(), asset.getId(), asset.isActive()));
            }
        }

        return out;
    }

    @PostMapping(value = "/api/booking/bookedby", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserBookingResponse getBookedByUser(@RequestBody WebUserBookingRequest request) {
        tokenRepository.clearTokens();

        ZamUser coord = this.tokenRepository.findUser(request.token());

        if(coord == null) {
            return new WebUserBookingResponse(false, "No such user", null);
        }

        Optional<ZamUser> user = this.userRepository.findById(request.userID());
        if(user.isEmpty()) {
            return new WebUserBookingResponse(false, "No such user", null);
        }

        if(coord.getTipo() == ZamUserType.DIPENDENTE) {
            return new WebUserBookingResponse(false, "Not allowed", null);
        } else if (coord.getTipo() == ZamUserType.COORDINATORE) {
            // I coordinatori possono vedere solo le prenotazioni dei propri utenti coordinati,
            // e non quelle di altri coordinatori o gestori.
            if(user.get().getCoordinatore().getId() != coord.getId() ||
                user.get().getTipo() != ZamUserType.DIPENDENTE) {
                return new WebUserBookingResponse(false, "Not allowed", null);
            }
        }

        Iterable<ZamBooking> bookings = bookingRepository.findAll();

        List<WebBookingAssoc> out = new ArrayList<>();
        for(ZamBooking booking : bookings) {
            if(booking.getIdUtente().getId() == user.get().getId()) {
                ZamAsset asset = booking.getIdAsset();
                out.add(new WebBookingAssoc(booking, asset.getNome(), asset.getPiano(), asset.getId(), asset.isActive()));
            }
        }

        return new WebUserBookingResponse(true, "OK", out);
    }

    @PostMapping(value = "/api/booking/active", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<WebBookingAssoc> getActive(@RequestBody WebBookingRequest request) {
        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());

        List<WebBookingAssoc> out = new ArrayList<>();

        Iterable<ZamBooking> bookings = bookingRepository.findAll();
        for(ZamBooking booking : bookings) {
            if(booking.getIdUtente().getId() == user.getId() && booking.getInizio().isAfter(LocalDateTime.now())) {
                ZamAsset asset = booking.getIdAsset();
                out.add(new WebBookingAssoc(booking, asset.getNome(), asset.getPiano(), asset.getId(), asset.isActive()));
            }
        }

        return out;
    }

    @PostMapping(value = "/api/booking/activeby", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserBookingResponse getActiveByUser(@RequestBody WebUserBookingRequest request) {
        tokenRepository.clearTokens();

        ZamUser coord = this.tokenRepository.findUser(request.token());

        if(coord == null) {
            return new WebUserBookingResponse(false, "No such user", null);
        }

        Optional<ZamUser> user = this.userRepository.findById(coord.getId());
        if(user.isEmpty()) {
            return new WebUserBookingResponse(false, "No such user", null);
        }

        if(coord.getTipo() == ZamUserType.DIPENDENTE) {
            return new WebUserBookingResponse(false, "Not allowed", null);
        } else if (coord.getTipo() == ZamUserType.COORDINATORE) {
            // I coordinatori possono vedere solo le prenotazioni dei propri utenti coordinati,
            // e non quelle di altri coordinatori o gestori.
            if(user.get().getCoordinatore().getId() != coord.getId() ||
                    user.get().getTipo() != ZamUserType.DIPENDENTE) {
                return new WebUserBookingResponse(false, "Not allowed", null);
            }
        }

        Iterable<ZamBooking> bookings = bookingRepository.findAll();

        List<WebBookingAssoc> out = new ArrayList<>();
        for(ZamBooking booking : bookings) {
            if(booking.getIdUtente().getId() == user.get().getId() && booking.getInizio().isAfter(LocalDateTime.now())) {
                ZamAsset asset = booking.getIdAsset();
                out.add(new WebBookingAssoc(booking, asset.getNome(), asset.getPiano(), asset.getId(), asset.isActive()));
            }
        }

        return new WebUserBookingResponse(true, "OK", out);
    }

    @PostMapping(value = "/api/booking/inactive", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Iterable<WebBookingAssoc> getInactive(@RequestBody WebBookingRequest request) {
        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());

        List<WebBookingAssoc> out = new ArrayList<>();

        Iterable<ZamBooking> bookings = bookingRepository.findAll();
        for(ZamBooking booking : bookings) {
            if(booking.getIdUtente().getId() == user.getId() && booking.getInizio().isBefore(LocalDateTime.now())) {
                ZamAsset asset = booking.getIdAsset();
                out.add(new WebBookingAssoc(booking, asset.getNome(), asset.getPiano(), asset.getId(), asset.isActive()));
            }
        }

        return out;
    }

    @PostMapping(value = "/api/booking/inactiveby", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebUserBookingResponse getInactiveByUser(@RequestBody WebUserBookingRequest request) {
        tokenRepository.clearTokens();

        ZamUser coord = this.tokenRepository.findUser(request.token());

        if(coord == null) {
            return new WebUserBookingResponse(false, "No such user", null);
        }

        Optional<ZamUser> user = this.userRepository.findById(coord.getId());
        if(user.isEmpty()) {
            return new WebUserBookingResponse(false, "No such user", null);
        }

        if(coord.getTipo() == ZamUserType.DIPENDENTE) {
            return new WebUserBookingResponse(false, "Not allowed", null);
        } else if (coord.getTipo() == ZamUserType.COORDINATORE) {
            // I coordinatori possono vedere solo le prenotazioni dei propri utenti coordinati,
            // e non quelle di altri coordinatori o gestori.
            if(user.get().getCoordinatore().getId() != coord.getId() ||
                    user.get().getTipo() != ZamUserType.DIPENDENTE) {
                return new WebUserBookingResponse(false, "Not allowed", null);
            }
        }

        Iterable<ZamBooking> bookings = bookingRepository.findAll();

        List<WebBookingAssoc> out = new ArrayList<>();
        for(ZamBooking booking : bookings) {
            if(booking.getIdUtente().getId() == user.get().getId() && booking.getInizio().isBefore(LocalDateTime.now())) {
                ZamAsset asset = booking.getIdAsset();
                out.add(new WebBookingAssoc(booking, asset.getNome(), asset.getPiano(), asset.getId(), asset.isActive()));
            }
        }

        return new WebUserBookingResponse(true, "OK", out);
    }

    @PostMapping(value = "/api/booking/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse deleteBooking(@RequestBody WebBookingDeleteRequest request) {
        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());

        for(ZamBooking booking : bookingRepository.findAll()) {
            if(request.bookingID().equals(booking.getId()) && booking.getIdUtente().getId() == user.getId()) {
                bookingRepository.delete(booking);
                return new WebGenericResponse(true, "OK");
            }
        }

        return new WebGenericResponse(false, "Prenotazione non trovata!");
    }

    @PostMapping(value = "/api/booking/deletefor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse deleteBookingForOther(@RequestBody WebBookingDeleteRequest request) {
        tokenRepository.clearTokens();

        ZamUser user = this.tokenRepository.findUser(request.token());

        if(user.getTipo() == ZamUserType.DIPENDENTE) {
            return new WebGenericResponse(false, "Not allowed");
        }

        if(user.getTipo() == ZamUserType.COORDINATORE) {
            for(ZamBooking booking : bookingRepository.findAll()) {
                if(request.bookingID().equals(booking.getId()) && booking.getIdUtente().getCoordinatore().getId() == user.getId()) {
                    bookingRepository.delete(booking);
                    return new WebGenericResponse(true, "OK");
                }
            }
        } else if(user.getTipo() == ZamUserType.GESTORE) {
            for(ZamBooking booking : bookingRepository.findAll()) {
                if(request.bookingID().equals(booking.getId())) {
                    bookingRepository.delete(booking);
                    return new WebGenericResponse(true, "OK");
                }
            }
        }


        return new WebGenericResponse(false, "Prenotazione non trovata!");
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

    private WebGenericResponse validateDates(ZonedDateTime start, ZonedDateTime end) {
        if(start.isAfter(end) || start.equals(end) || end.isBefore(start)) {
            ZamLogger.warning("Date condition 1");
            return new WebGenericResponse(false, "Date inserite non valide!");
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Rome"));
        if(start.isBefore(now) && !start.isEqual(now)) {
            ZamLogger.warning("Date condition 2 " + now);
            return new WebGenericResponse(false, "Date inserite non valide!");
        }

        return null;
    }

    @PostMapping(value = "/api/booking/book", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse book(@RequestBody WebBookingRequest request) {
        tokenRepository.clearTokens();

        ZonedDateTime start = convertDate(request.start());
        ZonedDateTime end = convertDate(request.end());

        WebGenericResponse validation = validateDates(start, end);
        if(validation != null) {
            return validation;
        }

        ZamToken token = tokenRepository.findZamTokenByVal(request.token());
        ZamUser user = this.tokenRepository.findUser(request.token());
        Optional<ZamAsset> asset = assetRepository.findById(request.asset());

        if(token == null || user == null || asset.isEmpty()) {
            ZamLogger.error("DATIMANC: " + token + user + asset.isEmpty());
            return new WebGenericResponse(false, "Dati mancanti!");
        }

        if(!asset.get().isActive()) {
            return new WebGenericResponse(false, "Asset non attivo!");
        }

        Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(asset.get());

        switch(user.getTipo()) {
            case ZamUserType.GESTORE:
                {
                    return new WebGenericResponse(false, "I gestori non possono effettuare prenotazioni!");
                }
            case ZamUserType.DIPENDENTE:
                {
                    List<ZamBooking> active = getNumActiveForUser(user);
                    int count = active.size();

                    ZamLogger.log("COUNT " + count + " has: " + bookingsHaveAsset(active, asset.get()));

                    // l'utente «dipendente» potrà prenotare solo un asset
                    if(count >= 1 && bookingsHaveAsset(active, asset.get())) {
                        return new WebGenericResponse(false, "Numero massimo di prenotazioni raggiunto.");
                    }

                    if(asset.get().getTipo() == ZamAssetType.C) {
                        return new WebGenericResponse(false, "Gli asset di tipo C non possono essere prenotati dai dipendenti!");
                    }
                }
                break;
            case ZamUserType.COORDINATORE:
                {
                    List<ZamBooking> active = getNumActiveForUser(user);
                    int count = active.size();

                    // mentre l'utente «coordinatore» potrà prenotarne fino a 3 in contemporanea
                    if(count >= 3 && bookingsHaveAsset(active, asset.get())) {
                        return new WebGenericResponse(false, "Numero massimo di prenotazioni raggiunto.");
                    }
                }
                break;
        }

        if (checkDoubleBooking(bookings, start, end)) {
            return new WebGenericResponse(false, "L'asset è già prenotato nella fascia oraria!");
        }

        ZamBooking booking = new ZamBooking(user, asset.get(), start.toLocalDateTime(), end.toLocalDateTime());
        bookingRepository.save(booking);

        return new WebGenericResponse(true, "OK");
    }

    @PostMapping(value = "/api/booking/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse editBooking(@RequestBody WebBookingEditRequest request) {
        tokenRepository.clearTokens();

        ZonedDateTime start = convertDate(request.start());
        ZonedDateTime end = convertDate(request.end());

        WebGenericResponse validation = validateDates(start, end);
        if(validation != null) {
            return validation;
        }

        ZamToken token = tokenRepository.findZamTokenByVal(request.token());
        ZamUser user = this.tokenRepository.findUser(request.token());
        Optional<ZamBooking> booking = bookingRepository.findById(request.bookingID());

        if(token == null || user == null || booking.isEmpty()) {
            return new WebGenericResponse(false, "Dati mancanti!");
        }

        if(!booking.get().getIdAsset().isActive()) {
            return new WebGenericResponse(false, "Asset non attivo!");
        }

        switch(user.getTipo()) {
            case ZamUserType.GESTORE:
                break;
            case ZamUserType.DIPENDENTE, ZamUserType.COORDINATORE:
            {
                if(user.getId() != booking.get().getIdUtente().getId()) {
                    return new WebGenericResponse(false, "Utente errato.");
                }

                if(booking.get().getNmod() >= 2) {
                    return new WebGenericResponse(false, "Numero massimo di modifiche superato!");
                }
            }
            break;
        }

        //Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(booking.get().getIdAsset());
        //if (checkDoubleBooking(bookings, start, end)) {
        //    return new WebGenericResponse(false, "L'asset è già prenotato nella fascia oraria!");
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

        return new WebGenericResponse(true, "OK");
    }

    @PostMapping(value = "/api/booking/editfor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public WebGenericResponse editBookingForUser(@RequestBody WebBookingEditRequest request) {
        tokenRepository.clearTokens();

        ZonedDateTime start = convertDate(request.start());
        ZonedDateTime end = convertDate(request.end());

        WebGenericResponse validation = validateDates(start, end);
        if(validation != null) {
            return validation;
        }

        ZamToken token = tokenRepository.findZamTokenByVal(request.token());
        ZamUser user = this.tokenRepository.findUser(request.token());
        Optional<ZamBooking> booking = bookingRepository.findById(request.bookingID());

        if(token == null || user == null || booking.isEmpty()) {
            return new WebGenericResponse(false, "Dati mancanti!");
        }

        if(user.getTipo() == ZamUserType.DIPENDENTE) {
            return new WebGenericResponse(false, "Not allowed");
        }

        if(!booking.get().getIdAsset().isActive()) {
            return new WebGenericResponse(false, "Asset non attivo!");
        }

        switch(user.getTipo()) {
            case ZamUserType.GESTORE:
                break;
            case ZamUserType.DIPENDENTE, ZamUserType.COORDINATORE:
            {
                if(user.getId() != booking.get().getIdUtente().getCoordinatore().getId()) {
                    return new WebGenericResponse(false, "Utente errato.");
                }

                if(booking.get().getNmod() >= 2) {
                    return new WebGenericResponse(false, "Numero massimo di modifiche superato!");
                }
            }
            break;
        }

        //Iterable<ZamBooking> bookings = bookingRepository.findZamBookingByIdAsset(booking.get().getIdAsset());
        //if (checkDoubleBooking(bookings, start, end)) {
        //    return new WebGenericResponse(false, "L'asset è già prenotato nella fascia oraria!");
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

        return new WebGenericResponse(true, "OK");
    }

    public WebBookingController(ZamBookingRepository bookingRepository, ZamAssetRepository assetRepository,
                                ZamUserRepository userRepository, ZamTokenRepository tokenRepository) {
        this.bookingRepository = bookingRepository;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }
}
