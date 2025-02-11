package com.zam.backend;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionToken {
    // Scadenza di un token in giorni
    private final int EXPIRES_IN = 30;

    private String token;
    private LocalDateTime createdAt;
    private ZamUser user;

    public SessionToken(ZamUser user) {
        this.token = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.user = user;
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(createdAt.plusSeconds(EXPIRES_IN));
    }

    @Override
    public String toString() {
        return token;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public LocalDateTime getCreationTimestamp() {
        return createdAt;
    }
}
