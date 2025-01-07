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

    @GetMapping("/getCaptcha")
    public WebCaptchaRequest getCaptcha() {
        try {
            Captcha captcha = new Captcha();
            captchas.add(captcha);
            return new WebCaptchaRequest(true, captcha.getId(), "/getCaptchaImage?id=" + captcha.getId(),
                    LocalDateTime.now());
        } catch(Exception e) {
            Logger.getGlobal().severe("getCaptcha failed with " + e.getMessage());
            return new WebCaptchaRequest(false, e.getMessage(), "", LocalDateTime.now());
        }
    }

    @GetMapping("/validateCaptcha")
    public WebCaptchaValidation validateCaptcha(@RequestParam String id, @RequestParam String match) {
        // Liberiamoci dei captcha scaduti prima di tutto
        LocalDateTime now = LocalDateTime.now();

        Iterator<Captcha> iterator = captchas.iterator();
        while(iterator.hasNext()) {
            Captcha captcha = iterator.next();
            if(captcha.getTimestamp().isBefore(now.minus(5, ChronoUnit.MINUTES))) {
                iterator.remove();
            }
        }

        for(Captcha captcha : captchas) {
            if(captcha.getId().equals(id)) {
                if(captcha.getMatch().equals(match)) {
                    captchas.removeIf(c -> c.getId().equals(id));
                    return new WebCaptchaValidation(true, id, LocalDateTime.now(), "OK");
                } else {
                    captchas.removeIf(c -> c.getId().equals(id));
                    return new WebCaptchaValidation(false, id, LocalDateTime.now(), "Captcha does not match");
                }
            }
        }

        captchas.removeIf(c -> c.getId().equals(id));
        return new WebCaptchaValidation(false, id, LocalDateTime.now(), "No such Captcha");
    }

    @GetMapping(value = "/getCaptchaImage", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] getCaptchaImage(@RequestParam String id) throws IOException {
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
