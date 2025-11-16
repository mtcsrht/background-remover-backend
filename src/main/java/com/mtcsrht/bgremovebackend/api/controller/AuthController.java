package com.mtcsrht.bgremovebackend.api.controller;


import com.mtcsrht.bgremovebackend.api.model.Dto.Auth.AuthResponse;
import com.mtcsrht.bgremovebackend.api.model.Dto.Auth.LoginRequest;
import com.mtcsrht.bgremovebackend.api.model.Dto.Auth.MeResponse;
import com.mtcsrht.bgremovebackend.api.model.Dto.Auth.RegisterRequest;
import com.mtcsrht.bgremovebackend.api.model.User;
import com.mtcsrht.bgremovebackend.api.repository.UserRepository;
import com.mtcsrht.bgremovebackend.api.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MeResponse body = new MeResponse();
        body.setId(user.getId());
        body.setUsername(user.getUsername());
        body.setEmail(user.getEmail());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // later error body
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Date accessExp = jwtService.getExpiration(accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        AuthResponse body = new AuthResponse(accessToken, accessExp.getTime());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(body);

    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        User user = userRepository.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Date accesExp = jwtService.getExpiration(accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        AuthResponse body = new AuthResponse(accessToken,accesExp.getTime());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null || !jwtService.isRefreshToken(refreshToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long userId;
        try {
            userId = jwtService.getUserId(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        Date accesExp = jwtService.getExpiration(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        AuthResponse body = new AuthResponse(newAccessToken,accesExp.getTime());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}
