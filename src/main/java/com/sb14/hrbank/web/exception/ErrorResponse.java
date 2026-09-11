package com.sb14.hrbank.web.exception;

import com.sb14.hrbank.domain.exception.HrBankExceptionType;

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
}
