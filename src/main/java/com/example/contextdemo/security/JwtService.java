package com.example.contextdemo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final SecretKey key;
    private final long hours;
    public JwtService(@Value("${app.jwt-secret}") String secret, @Value("${app.token-hours}") long hours) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.hours = hours;
    }
    public String create(String username) {
        Instant now = Instant.now();
        return Jwts.builder().subject(username).id(UUID.randomUUID().toString())
            .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(hours * 3600)))
            .signWith(key).compact();
    }
    public io.jsonwebtoken.Claims parse(String jwt) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
    }
}
