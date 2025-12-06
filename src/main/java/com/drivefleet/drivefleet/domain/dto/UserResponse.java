package com.drivefleet.drivefleet.domain.dto;

import com.drivefleet.drivefleet.domain.enums.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) {}

