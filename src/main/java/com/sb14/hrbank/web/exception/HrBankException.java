package com.sb14.hrbank.web.exception;

import lombok.Getter;

@Getter
public class HrBankException extends RuntimeException {

    private final HrBankExceptionType type;

    public HrBankException(HrBankExceptionType type, String message) {
        super(message);
        this.type = type;
    }
}
