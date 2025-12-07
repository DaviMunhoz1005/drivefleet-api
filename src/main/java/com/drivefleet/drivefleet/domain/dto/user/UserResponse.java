package com.drivefleet.drivefleet.domain.dto.user;

import com.drivefleet.drivefleet.domain.enums.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) {}

