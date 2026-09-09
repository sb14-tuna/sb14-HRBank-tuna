package com.sb14.hrbank.web.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum HrBankExceptionType {

    TMP_EXCEPTION(
            Level.ERROR,
            HttpStatus.BAD_REQUEST,
            "bad request 입니다"
    );

    Level logLevel;
    HttpStatus status;
    String response;

}
