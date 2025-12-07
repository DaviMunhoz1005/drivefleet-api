package com.drivefleet.drivefleet.exceptions;

import org.springframework.http.HttpStatus;

public class SellerCannotBeExcludedException extends DomainException {

    public SellerCannotBeExcludedException(String id) {
        super(
                "SELLER-CANNOT-BE-DELETED-" + HttpStatus.CONFLICT.value(),
                "Seller with id " + id + " cannot be deleted because it has linked sales orders",
                HttpStatus.CONFLICT
        );
    }
}
