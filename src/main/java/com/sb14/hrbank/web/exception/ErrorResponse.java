package com.sb14.hrbank.web.exception;

import java.time.Instant;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String message,
    String details
) {
    public static ErrorResponse of(HrBankExceptionType type, String details){
        return new ErrorResponse(
            Instant.now(),
            type.getStatus().value(),
            type.getResponse(),
            details
        );
    }
}
