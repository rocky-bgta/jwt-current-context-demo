package com.example.contextdemo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("room_context")
public class RoomContext {
    @Id public String id;
    public String username;
    public String jwtId;
    public String roomId;
    public boolean active;
    public Instant createdAt;
    public RoomContext() {}
    public RoomContext(String id, String username, String jwtId, String roomId) {
        this.id = id;
        this.username = username;
        this.jwtId = jwtId;
        this.roomId = roomId;
        this.active = true;
        this.createdAt = Instant.now();
    }
}
