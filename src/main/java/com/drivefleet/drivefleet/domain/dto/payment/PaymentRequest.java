package com.drivefleet.drivefleet.domain.dto.payment;

import com.drivefleet.drivefleet.domain.enums.PaymentMethod;
import com.drivefleet.drivefleet.domain.enums.PaymentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentRequest(

        @NotNull(message = "Payment date is required")
        @PastOrPresent(message = "Payment date cannot be in the future")
        LocalDate paymentDate,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Payment method is required")
        PaymentMethod method,

        @NotNull(message = "Payment status is required")
        PaymentStatus status,

        @NotNull(message = "Sales order ID is required")
        UUID salesOrderId
) {}
