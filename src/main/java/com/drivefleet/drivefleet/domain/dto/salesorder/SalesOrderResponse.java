package com.drivefleet.drivefleet.domain.dto.salesorder;

import com.drivefleet.drivefleet.domain.dto.vehicle.VehicleSummaryResponse;
import com.drivefleet.drivefleet.domain.dto.customer.CustomerSummaryResponse;
import com.drivefleet.drivefleet.domain.dto.payment.PaymentResponse;
import com.drivefleet.drivefleet.domain.dto.seller.SellerSummaryResponse;
import com.drivefleet.drivefleet.domain.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record SalesOrderResponse(
        UUID id,
        LocalDate creationDate,
        LocalDate conclusionDate,
        BigDecimal totalValue,
        OrderStatus status,
        SellerSummaryResponse seller,
        CustomerSummaryResponse customer,
        VehicleSummaryResponse vehicle,
        PaymentResponse payment
) {}
