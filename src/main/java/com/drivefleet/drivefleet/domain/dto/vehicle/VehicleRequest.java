package com.drivefleet.drivefleet.domain.dto.vehicle;

import com.drivefleet.drivefleet.domain.enums.VehicleStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record VehicleRequest(

        @NotBlank(message = "Brand is required")
        String brand,

        @NotBlank(message = "Model is required")
        String model,

        @NotNull(message = "Manufacture year is required")
        @Min(value = 1900, message = "Invalid manufacture year")
        Integer yearManufacture,

        @NotNull(message = "Model year is required")
        @Min(value = 1900, message = "Invalid model year")
        Integer yearModel,

        @NotBlank(message = "Plate is required")
        String plate,

        @NotBlank(message = "Color is required")
        String color,

        @NotNull(message = "Mileage is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Mileage cannot be negative")
        BigDecimal mileage,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Status is required")
        VehicleStatus status,

        @NotNull(message = "Sales order ID is required")
        UUID salesOrderId
) {}
