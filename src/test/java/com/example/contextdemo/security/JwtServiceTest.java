package com.example.contextdemo.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private static final String SECRET = "this-is-a-local-test-secret-of-sufficient-length-123456";

    @Test
    void createdTokenHasSubjectAndId() {
        JwtService service = new JwtService(SECRET, 2);
        var claims = service.parse(service.create("alice"));
        assertEquals("alice", claims.getSubject());
        assertNotNull(claims.getId());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void tokenSignedWithDifferentSecretIsRejected() {
        String token = new JwtService(SECRET, 2).create("alice");
        JwtService other = new JwtService("another-local-test-secret-of-sufficient-length-98765", 2);
        assertThrows(JwtException.class, () -> other.parse(token));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService service = new JwtService(SECRET, -1);
        assertThrows(JwtException.class, () -> service.parse(service.create("alice")));
    }
}
