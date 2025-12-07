package com.drivefleet.drivefleet.domain.dto.seller;

import com.drivefleet.drivefleet.domain.dto.salesorder.SalesOrderResponse;
import com.drivefleet.drivefleet.domain.dto.user.UserResponse;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SellerResponse(
        UUID id,
        Long registrationNumber,
        UserResponse user,
        List<SalesOrderResponse> sales
) {}
