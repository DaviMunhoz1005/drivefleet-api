package com.drivefleet.drivefleet.domain.dto.customer;

import com.drivefleet.drivefleet.domain.dto.user.UserResponse;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerResponse(
        UUID id,
        Long cpf,
        Long phone,
        String address,
        UserResponse user
) {}
