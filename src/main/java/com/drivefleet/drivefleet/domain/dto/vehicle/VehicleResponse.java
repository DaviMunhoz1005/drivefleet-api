package com.drivefleet.drivefleet.domain.dto.vehicle;

import com.drivefleet.drivefleet.domain.enums.VehicleStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record VehicleResponse(
        UUID id,
        String brand,
        String model,
        Integer yearManufacture,
        Integer yearModel,
        String plate,
        String color,
        BigDecimal mileage,
        BigDecimal price,
        VehicleStatus status,
        UUID salesOrderId
) {}
