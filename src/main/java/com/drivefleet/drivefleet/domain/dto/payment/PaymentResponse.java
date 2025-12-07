package com.drivefleet.drivefleet.domain.dto.payment;

import com.drivefleet.drivefleet.domain.enums.PaymentMethod;
import com.drivefleet.drivefleet.domain.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PaymentResponse(
        UUID id,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal price,
        LocalDate paymentDate,
        UUID salesOrderId
) {}
