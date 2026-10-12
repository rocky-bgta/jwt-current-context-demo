package com.example.contextdemo.service;

import com.example.contextdemo.model.RoomContext;
import com.example.contextdemo.repository.RoomContextRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomContextService {
    private final RoomContextRepository repository;
    private final StringRedisTemplate redis;

    public RoomContextService(RoomContextRepository repository,
                              StringRedisTemplate redis) {
        this.repository = repository;
        this.redis = redis;
    }

    public String createContext(String room, String username, String jwtId) {
        // One context ID belongs to one room and one JWT session.
        String contextId = UUID.randomUUID().toString();

        RoomContext context = new RoomContext(contextId, username, jwtId, room);
        repository.save(context);

        String redisKey = "ctx:" + contextId;
        String redisValue = jwtId + ":" + room;
        redis.opsForValue().set(redisKey, redisValue, Duration.ofHours(2));

        return contextId;
    }

    public void checkContext(String room, String contextId,
                             String username, String jwtId) {
        if (contextId == null || contextId.isBlank()) {
            throw new AccessDeniedException("You need a context ID for Room " + room);
        }

        Optional<RoomContext> found = repository.findById(contextId);
        if (found.isEmpty()) {
            throw new AccessDeniedException("Context ID not found");
        }

        RoomContext context = found.get();

        if (!context.active) {
            throw new AccessDeniedException("Context has been revoked");
        }
        if (!room.equals(context.roomId)) {
            throw new AccessDeniedException("This context belongs to another room");
        }
        if (!username.equals(context.username)) {
            throw new AccessDeniedException("This context belongs to another user");
        }
        if (!jwtId.equals(context.jwtId)) {
            throw new AccessDeniedException("This context belongs to another JWT session");
        }

        String redisValue = redis.opsForValue().get("ctx:" + contextId);
        String expectedValue = jwtId + ":" + room;
        if (!expectedValue.equals(redisValue)) {
            throw new AccessDeniedException("Context is expired or revoked");
        }
    }

    public void revokeContext(String room, String contextId,
                              String username, String jwtId) {
        // Validate ownership and active status before revoking.
        checkContext(room, contextId, username, jwtId);

        RoomContext context = repository.findById(contextId).get();
        context.active = false;
        repository.save(context);

        redis.delete("ctx:" + contextId);
    }
}
