package com.drivefleet.drivefleet.domain.dto.customer;

import com.drivefleet.drivefleet.domain.dto.user.UserRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CustomerRequest(

        @NotNull(message = "CPF is required")
        @Digits(integer = 11, fraction = 0, message = "CPF must have up to 11 digits")
        Long cpf,

        @NotNull(message = "Phone is required")
        @Digits(integer = 11, fraction = 0, message = "Phone must have up to 11 digits")
        Long phone,

        @NotBlank(message = "Address is required")
        String address,

        @Valid
        @NotNull(message = "User data is required")
        UserRequest user
) {}
