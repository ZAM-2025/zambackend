package com.zam.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.zam.captcha.Captcha;

import javax.imageio.ImageIO;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class WebCaptchaController {
    private static ArrayList<Captcha> captchas = new ArrayList<>();
    private static ArrayList<Captcha> validatedCaptchas = new ArrayList<>();
    // Intervallo di scadenza dei Captcha (ogni CAPTCHA_EXPIRY minuti)
    private static final int CAPTCHA_EXPIRY = 1;

    public static ArrayList<Captcha> getValidatedCaptchas() {
        return validatedCaptchas;
    }

    public static void removeValidatedCaptcha(String captchaID) {
        validatedCaptchas.removeIf(captcha -> captchaID.equals(captcha.getId()));
    }

    // Rimuove i Captcha scaduti.
    // Ideale farlo ogni volta che viene chiamato un metodo che interagisce con i Captcha
    private void WipeCaptchas() {
        LocalDateTime now = LocalDateTime.now();

        captchas.removeIf(captcha -> captcha.getTimestamp().isBefore(now.minusMinutes(CAPTCHA_EXPIRY)));
    }

    @GetMapping("/api/captcha/get")
    public WebCaptchaRequest getCaptcha() {
        this.WipeCaptchas();

        try {
            Captcha captcha = new Captcha();
            captchas.add(captcha);
            return new WebCaptchaRequest(true, captcha.getId(), "/api/captcha/image?id=" + captcha.getId(),
                    LocalDateTime.now());
        } catch(Exception e) {
            Logger.getGlobal().severe("getCaptcha failed with " + e.getMessage());
            return new WebCaptchaRequest(false, e.getMessage(), "", LocalDateTime.now());
        }
    }

    @GetMapping("/api/captcha/validate")
    public WebCaptchaValidation validateCaptcha(@RequestParam String id, @RequestParam String match) {
        // Liberiamoci dei captcha scaduti prima di tutto
        this.WipeCaptchas();

        for(Captcha captcha : captchas) {
            if(captcha.getId().equals(id)) {
                if(captcha.getMatch().equals(match)) {
                    validatedCaptchas.add(captcha);
                    captchas.removeIf(c -> c.getId().equals(id));
                    return new WebCaptchaValidation(true, id, LocalDateTime.now(), "OK");
                } else {
                    captchas.removeIf(c -> c.getId().equals(id));
                    return new WebCaptchaValidation(false, id, LocalDateTime.now(), "Captcha errato!");
                }
            }
        }

        captchas.removeIf(c -> c.getId().equals(id));
        return new WebCaptchaValidation(false, id, LocalDateTime.now(), "Captcha scaduto!");
    }

    @GetMapping(value = "/api/captcha/image", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getCaptchaImage(@RequestParam String id) throws IOException {
        this.WipeCaptchas();

        for (Captcha c : captchas) {
            if (c.getId().equals(id)) {
                ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
                ImageIO.write(c.getAsBufferedImage(), "PNG", imgStream);

                return imgStream.toByteArray();
            }
        }

        return null;
    }
}
