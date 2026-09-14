package com.sb14.hrbank.web.exception;

import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HrBankException.class)
    public ResponseEntity<ErrorResponse> handleHrBankDomainException(HrBankException e){
        log.warn("서비스 로직 처리 중 예외 발생 ------- message : {}", e.getMessage());
        HrBankExceptionType type = e.getType();
        String message = e.getMessage();

        return ResponseEntity.status(type.getStatus())
            .body(ErrorResponse.of(type));
    }
}
