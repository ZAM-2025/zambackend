package com.zam.backend;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.zam.captcha.Captcha;

import javax.imageio.ImageIO;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Logger;

@RestController
public class WebCaptchaController {
    private static ArrayList<Captcha> captchas = new ArrayList<>();

    @CrossOrigin(origins = "http://localhost:8000")
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

    @CrossOrigin(origins = "http://localhost:8000")
    @GetMapping("/validateCaptcha")
    public WebCaptchaValidation validateCaptcha(@RequestParam String id, @RequestParam String match) {
        for(Captcha captcha : captchas) {
            if(captcha.getId().equals(id)) {
                if(captcha.getMatch().equals(match)) {
                    captchas.removeIf(c -> c.getId().equals(id));
                    return new WebCaptchaValidation(true, id, LocalDateTime.now(), "OK");
                } else {
                    return new WebCaptchaValidation(false, id, LocalDateTime.now(), "Captcha does not match");
                }
            }
        }

        return new WebCaptchaValidation(false, id, LocalDateTime.now(), "No such Captcha");
    }

    @CrossOrigin(origins = "http://localhost:8000")
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
