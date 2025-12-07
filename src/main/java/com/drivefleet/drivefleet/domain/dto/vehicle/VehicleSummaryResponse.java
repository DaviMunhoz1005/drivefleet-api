package com.drivefleet.drivefleet.domain.dto.vehicle;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record VehicleSummaryResponse(
        UUID id,
        String brand,
        String model,
        Integer yearManufacture,
        Integer yearModel,
        BigDecimal price
) {}
