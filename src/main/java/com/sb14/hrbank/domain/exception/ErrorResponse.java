package com.sb14.hrbank.domain.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String message,
    String details
) {
    public static ErrorResponse of(HrBankExceptionType type){
        return new ErrorResponse(
            Instant.now(),
            type.getStatus().value(),
            type.getMessage(),
            type.getDetails()
        );
    }

    public static ErrorResponse of(HttpStatus status, String message, String details) {
        return new ErrorResponse(
                Instant.now(),
                status.value(),
                message,
                details
        );
    }
}
