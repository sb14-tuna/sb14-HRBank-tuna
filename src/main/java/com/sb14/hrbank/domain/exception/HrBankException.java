package com.sb14.hrbank.domain.exception;

import lombok.Getter;

@Getter
public class HrBankException extends RuntimeException {

    private final HrBankExceptionType type;

    public HrBankException(HrBankExceptionType type, String logMessage) {
        super(logMessage);            // 개발자 로그 메시지
        this.type = type;
    }
}
