package com.example.contextdemo.service;

import com.example.contextdemo.model.RoomContext;
import com.example.contextdemo.repository.RoomContextRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomContextServiceTest {
    private final RoomContextRepository repository = mock(RoomContextRepository.class);
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> values = mock(ValueOperations.class);
    private final RoomContextService service = new RoomContextService(repository, redis);

    @Test
    void matchingRoomUserAndSessionAllowsAccess() {
        RoomContext context = new RoomContext("ctx-1", "alice", "jwt-1", "A");
        when(repository.findById("ctx-1")).thenReturn(Optional.of(context));
        when(redis.opsForValue()).thenReturn(values);
        when(values.get("ctx:ctx-1")).thenReturn("jwt-1:A");
        assertDoesNotThrow(() -> service.checkContext("A", "ctx-1", "alice", "jwt-1"));
    }

    @Test
    void contextCannotBeUsedForAnotherRoom() {
        when(repository.findById("ctx-1")).thenReturn(Optional.of(
                new RoomContext("ctx-1", "alice", "jwt-1", "A")));
        assertThrows(AccessDeniedException.class,
                () -> service.checkContext("C", "ctx-1", "alice", "jwt-1"));
    }

    @Test
    void contextCannotBeUsedByAnotherUserOrSession() {
        when(repository.findById("ctx-1")).thenReturn(Optional.of(
                new RoomContext("ctx-1", "alice", "jwt-1", "A")));
        assertThrows(AccessDeniedException.class,
                () -> service.checkContext("A", "ctx-1", "bob", "jwt-1"));
        assertThrows(AccessDeniedException.class,
                () -> service.checkContext("A", "ctx-1", "alice", "jwt-2"));
    }

    @Test
    void revokedContextIsRejected() {
        RoomContext context = new RoomContext("ctx-1", "alice", "jwt-1", "A");
        context.active = false;
        when(repository.findById("ctx-1")).thenReturn(Optional.of(context));
        assertThrows(AccessDeniedException.class,
                () -> service.checkContext("A", "ctx-1", "alice", "jwt-1"));
    }

    @Test
    void missingRedisEntryRejectsContext() {
        when(repository.findById("ctx-1")).thenReturn(Optional.of(
                new RoomContext("ctx-1", "alice", "jwt-1", "A")));
        when(redis.opsForValue()).thenReturn(values);
        assertThrows(AccessDeniedException.class,
                () -> service.checkContext("A", "ctx-1", "alice", "jwt-1"));
    }
}
