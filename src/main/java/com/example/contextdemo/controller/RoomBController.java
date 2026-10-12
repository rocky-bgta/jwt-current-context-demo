package com.example.contextdemo.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/B")
public class RoomBController {
    // The JWT filter already authenticated the caller.
    // Room B intentionally does NOT require a current context.
    @GetMapping
    public Map<String, String> enterRoom() {
        return Map.of("message", "Welcome to Room B (JWT only)");
    }
}
