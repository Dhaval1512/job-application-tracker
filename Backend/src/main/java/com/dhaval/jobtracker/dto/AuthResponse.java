package com.dhaval.jobtracker.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs
) {}