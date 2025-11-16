package com.mtcsrht.bgremovebackend.api.model.Dto.Auth;

public class AuthResponse {
    private final String accessToken;
    private long expiresAt;

    public AuthResponse(String accessToken, long expiresAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void getExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
