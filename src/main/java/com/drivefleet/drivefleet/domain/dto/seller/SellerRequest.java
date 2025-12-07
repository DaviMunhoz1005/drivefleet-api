package com.drivefleet.drivefleet.domain.dto.seller;

import com.drivefleet.drivefleet.domain.dto.user.UserRequest;
import jakarta.validation.Valid;

public record SellerRequest(
        @Valid
        UserRequest user
) {}
