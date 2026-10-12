package com.example.contextdemo.controller;

import com.example.contextdemo.service.AuthService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record LoginRequest(String username, String password) {}

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        return Map.of("accessToken",
                authService.login(request.username(), request.password()));
    }

    @PostMapping("/logout")
    public Map<String, String> logout(
            @RequestAttribute("jwtId") String jwtId) {
        authService.logout(jwtId);
        return Map.of("message", "JWT revoked. All rooms are now blocked.");
    }
}
