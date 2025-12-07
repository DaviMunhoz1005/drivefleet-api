package com.drivefleet.drivefleet.domain.dto.customer;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CustomerSummaryResponse(
        UUID id,
        String name,
        String email,
        Long phone
) {}
