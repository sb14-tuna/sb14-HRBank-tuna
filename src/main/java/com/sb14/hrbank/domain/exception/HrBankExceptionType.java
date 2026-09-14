package com.sb14.hrbank.domain.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum HrBankExceptionType {

    TMP_EXCEPTION(
            Level.ERROR,
            HttpStatus.BAD_REQUEST,
            "bad request 입니다",
        "bad"
    ),




    // 파일 관련
    FILE_NOT_FOUND(
        Level.ERROR,
        HttpStatus.NOT_FOUND,
        "해당 파일을 찾을 수 없습니다.",
        "bad"
    ),
    FILE_LOAD_FAILED(
        Level.ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "키 값으로 물리파일을 불러오는 것이 실패하였습니다."
        ,"bad"
    ),
    FILE_KEY_INVALID(
        Level.ERROR,
        HttpStatus.BAD_REQUEST,
        "파일 저장 키가 유효하지 않습니다."
        ,"bad"
    ),


    // 백업관련
    BACKUP_STATE_INVALID_CHANGE(
        Level.ERROR,
        HttpStatus.BAD_REQUEST,
        "백업 이력 상태를 변경할 수 없습니다."
        ,"bad"
    );

    Level logLevel;
    HttpStatus status;
    String message;
    String details;
}
