package com.sb14.hrbank.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

        return ResponseEntity.status(type.getStatus())
            .body(ErrorResponse.from(type));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleHrBankDomainException(Exception e){
        log.warn("서비스 로직 처리 중 예외 발생 ------- message : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)    // todo : 일단 400 예외로 잡히게
            .body(e.getMessage());
    }
}
