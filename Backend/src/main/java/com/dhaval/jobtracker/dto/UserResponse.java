package com.dhaval.jobtracker.dto;

import com.dhaval.jobtracker.entity.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String displayName,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt()
        );
    }
}