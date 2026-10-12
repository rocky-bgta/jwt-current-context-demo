package com.example.contextdemo.service;

import com.example.contextdemo.security.JwtService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final StringRedisTemplate redis;

    public AuthService(JwtService jwtService, StringRedisTemplate redis) {
        this.jwtService = jwtService;
        this.redis = redis;
    }

    public String login(String username, String password) {
        // Fixed credentials are only for this learning demo.
        if (!"demo".equals(username) || !"demo123".equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtService.create(username);
        String jwtId = jwtService.parse(token).getId();

        redis.opsForValue().set(
                "jwt:active:" + jwtId, username, Duration.ofHours(2));

        return token;
    }

    public void logout(String jwtId) {
        redis.delete("jwt:active:" + jwtId);
    }
}
