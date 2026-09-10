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
    ),






    // 파일 관련
    FILE_NOT_FOUND(
        Level.ERROR,
        HttpStatus.NOT_FOUND,
        "해당 파일을 찾을 수 없습니다."
    ),
    FILE_LOAD_FAILED(
        Level.ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "키 값으로 물리파일을 불러오는 것이 실패하였습니다."
    ),
    FILE_KEY_INVALID(
        Level.ERROR,
        HttpStatus.BAD_REQUEST,
        "파일 저장 키가 유효하지 않습니다."
    );

    Level logLevel;
    HttpStatus status;
    String response;

}
