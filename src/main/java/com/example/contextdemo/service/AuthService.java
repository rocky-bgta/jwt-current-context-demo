package com.example.contextdemo.service;

import com.example.contextdemo.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final StringRedisTemplate redis;
    private final PasswordEncoder encoder;
    private final String demoUsername;
    private final String demoPasswordHash;
    private final Duration tokenTtl;

    public AuthService(JwtService jwtService, StringRedisTemplate redis, PasswordEncoder encoder,
                       @Value("${app.demo-username:demo}") String demoUsername,
                       @Value("${app.demo-password}") String demoPassword,
                       @Value("${app.token-hours}") long tokenHours) {
        this.jwtService = jwtService;
        this.redis = redis;
        this.encoder = encoder;
        this.demoUsername = demoUsername;
        this.demoPasswordHash = encoder.encode(demoPassword);
        this.tokenTtl = Duration.ofHours(tokenHours);
    }

    public String login(String username, String password) {
        if (!demoUsername.equals(username) || !encoder.matches(password, demoPasswordHash)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        String token = jwtService.create(username);
        String jwtId = jwtService.parse(token).getId();
        redis.opsForValue().set("jwt:active:" + jwtId, username, tokenTtl);
        return token;
    }

    public void logout(String jwtId) {
        redis.delete("jwt:active:" + jwtId);
    }
}
