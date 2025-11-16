package com.mtcsrht.bgremovebackend.api.service;

import com.mtcsrht.bgremovebackend.api.model.User;
import org.junit.jupiter.api.Test;
import com.mtcsrht.bgremovebackend.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private final JwtService jwtService = new JwtService();

    private User dummyUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("dummy");
        u.setEmail("dummy@localhost.com");
        u.setPasswordHash("hashed");
        return u;
    }

    @Test
    void generateAccessToken_containsUserIdAndEmailAndTypeAccess(){
        User u = dummyUser();
        String token = jwtService.generateAccessToken(u);

        assertNotNull(token);

        Jws<Claims> parsed = jwtService.parseToken(token);
        Claims claims = parsed.getBody();

        assertEquals(String.valueOf(u.getId()), claims.getSubject());
        assertEquals(u.getEmail(), claims.get("email", String.class));
        assertEquals("access", claims.get("type", String.class));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateRefreshToken_containsUserIdAndEmailAndTypeRefresh() {
        User user = dummyUser();

        String token = jwtService.generateRefreshToken(user);

        assertNotNull(token);

        Jws<Claims> parsed = jwtService.parseToken(token);
        Claims claims = parsed.getBody();

        assertEquals(String.valueOf(user.getId()), claims.getSubject());
        assertEquals(user.getEmail(), claims.get("email", String.class));
        assertEquals("refresh", claims.get("type", String.class));
    }

    @Test
    void isAccessTokenAndIsRefreshTokenBehaveCorrectly() {
        User user = dummyUser();

        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        assertTrue(jwtService.isAccessToken(access));
        assertFalse(jwtService.isRefreshToken(access));

        assertTrue(jwtService.isRefreshToken(refresh));
        assertFalse(jwtService.isAccessToken(refresh));
    }

    @Test
    void parseToken_withInvalidTokenThrows() {
        String invalid = "this.is.not.a.jwt";

        assertThrows(Exception.class, () -> jwtService.parseToken(invalid));
    }

}
