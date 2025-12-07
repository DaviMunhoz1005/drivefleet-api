package com.drivefleet.drivefleet.exceptions;

import org.springframework.http.HttpStatus;

public class UserHasBeenExcludedException extends DomainException {

    public UserHasBeenExcludedException(String id) {
        super(
                "USER-EXCLUDED-" + HttpStatus.CONFLICT.value(),
                "User with id " + id + " has been excluded and cannot perform this operation",
                HttpStatus.CONFLICT
        );
    }
}
