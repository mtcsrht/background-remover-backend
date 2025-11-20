package com.mtcsrht.bgremovebackend.api.service;

import com.mtcsrht.bgremovebackend.api.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    // DEVELPOMENT
    private static final String SECRET = "super-secret-and-long-jwt-key-change-me-1234567890";


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        long accessTokenValiditySeconds = 15 * 60;
        Instant expiresAt = now.plusSeconds(accessTokenValiditySeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from((expiresAt)))
                .addClaims(Map.of(
                        "email", user.getEmail(),
                        "type", "access"
                ))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();

        long refreshTokenValiditySeconds = 7 * 24 * 60 * 60;
        Instant expiresAt = now.plusSeconds(refreshTokenValiditySeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from((expiresAt)))
                .addClaims(Map.of(
                        "email", user.getEmail(),
                        "type", "refresh"
                ))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token).getBody();
            return "refresh".equals(claims.get("type", String.class));
        } catch (JwtException ex) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseToken(token).getBody();
            return "access".equals(claims.get("type", String.class));
        } catch (JwtException ex) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token).getBody();
        return Long.valueOf(claims.getSubject());
    }

    public String getEmail(String token) {
        Claims claims = parseToken(token).getBody();
        return claims.get("email", String.class);
    }

    public Date getExpiration(String token) {
        return parseToken(token).getBody().getExpiration();
    }

}
