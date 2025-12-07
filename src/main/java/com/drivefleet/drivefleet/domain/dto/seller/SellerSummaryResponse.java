package com.drivefleet.drivefleet.domain.dto.seller;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SellerSummaryResponse(
        UUID id,
        Long registrationNumber,
        String name,
        String email
) {}
