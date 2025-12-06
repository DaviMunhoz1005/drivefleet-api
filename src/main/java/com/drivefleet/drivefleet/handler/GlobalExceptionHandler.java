package com.drivefleet.drivefleet.handler;

import com.drivefleet.drivefleet.exceptions.DomainException;
import com.drivefleet.drivefleet.exceptions.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                "https://drivefleet/errors/" + ex.getCode(),
                ex.getMessage(),
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI(),
                java.time.Instant.now()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }
}