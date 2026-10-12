package com.example.contextdemo.controller;

import com.example.contextdemo.service.RoomContextService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/C")
public class RoomCController {
    private final RoomContextService contextService;

    public RoomCController(RoomContextService contextService) {
        this.contextService = contextService;
    }

    @PostMapping("/contexts")
    public Map<String, String> createContext(Authentication user,
                                              @RequestAttribute("jwtId") String jwtId) {
        String contextId = contextService.createContext("C", user.getName(), jwtId);
        return Map.of("contextId", contextId, "room", "C");
    }

    @GetMapping
    public Map<String, String> enterRoom(
            @RequestHeader(value = "X-Current-Context", required = false) String contextId,
            Authentication user,
            @RequestAttribute("jwtId") String jwtId) {
        contextService.checkContext("C", contextId, user.getName(), jwtId);
        return Map.of("message", "Welcome to Room C");
    }

    @DeleteMapping("/contexts/{contextId}")
    public Map<String, String> revokeContext(@PathVariable String contextId,
                                              Authentication user,
                                              @RequestAttribute("jwtId") String jwtId) {
        contextService.revokeContext("C", contextId, user.getName(), jwtId);
        return Map.of("message", "Room C context revoked");
    }
}
