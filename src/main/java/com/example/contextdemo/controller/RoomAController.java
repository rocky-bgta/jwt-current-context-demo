package com.example.contextdemo.controller;

import com.example.contextdemo.service.RoomContextService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/A")
public class RoomAController {
    private final RoomContextService contextService;

    public RoomAController(RoomContextService contextService) {
        this.contextService = contextService;
    }

    @PostMapping("/contexts")
    public Map<String, String> createContext(Authentication user,
                                              @RequestAttribute("jwtId") String jwtId) {
        String contextId = contextService.createContext("A", user.getName(), jwtId);
        return Map.of("contextId", contextId, "room", "A");
    }

    @GetMapping
    public Map<String, String> enterRoom(
            @RequestHeader(value = "X-Current-Context", required = false) String contextId,
            Authentication user,
            @RequestAttribute("jwtId") String jwtId) {
        contextService.checkContext("A", contextId, user.getName(), jwtId);
        return Map.of("message", "Welcome to Room A");
    }

    @DeleteMapping("/contexts/{contextId}")
    public Map<String, String> revokeContext(@PathVariable String contextId,
                                              Authentication user,
                                              @RequestAttribute("jwtId") String jwtId) {
        contextService.revokeContext("A", contextId, user.getName(), jwtId);
        return Map.of("message", "Room A context revoked");
    }
}
