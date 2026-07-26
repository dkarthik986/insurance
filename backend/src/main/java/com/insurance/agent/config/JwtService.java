package com.insurance.agent.config;

import com.insurance.agent.auth.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessExpiry;
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-expiry-ms}") long accessExpiry) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiry = accessExpiry;
    }
    public String generateAccessToken(User user) {
        var now = new Date();
        return Jwts.builder().subject(user.getId().toString())
            .claim("role", user.getRole().name()).claim("email", user.getEmail())
            .issuedAt(now).expiration(new Date(now.getTime() + accessExpiry))
            .signWith(key).compact();
    }
    public String generateRefreshToken() { return UUID.randomUUID().toString(); }
    public boolean validateToken(String token) {
        try { claims(token); return true; } catch (JwtException | IllegalArgumentException ex) { return false; }
    }
    public UUID extractUserId(String token) { return UUID.fromString(claims(token).getSubject()); }
    public String extractRole(String token) { return claims(token).get("role", String.class); }
    public String extractEmail(String token) { return claims(token).get("email", String.class); }
    private Claims claims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}

