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

    // 직원 관련
    EMPLOYEE_NOT_FOUND(
            Level.ERROR,
            HttpStatus.NOT_FOUND,
            "잘못된 요청입니다.",
            "[EMPLOYEE] 해당 ID를 가진 직원을 찾을 수 없음"
    ),
    EMAIL_ALREADY_EXISTS(
            Level.ERROR,
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다.",
            "[EMPLOYEE] 해당 이메일을 가진 직원이 이미 존재함"
    ),
    ILLEGAL_DISTRIBUTION_GROUP(
            Level.ERROR,
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다.",
            "[EMPLOYEE] 지원하지 않는 분포 그룹"
    ),
    ILLEGAL_SORT_FIELD(
            Level.ERROR,
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다.",
            "[EMPLOYEE] 지원하지 않는 정렬 기준"
    ),
    ILLEGAL_DATE_FORMAT(
            Level.ERROR,
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다.",
            "[EMPLOYEE] 잘못된 날짜 형식"
    ),
    // 부서 관련
    DEPARTMENT_NOT_FOUND(
            Level.ERROR,
            HttpStatus.NOT_FOUND,
            "잘못된 요청입니다.",
            "[DEPARTMENT] 해당 ID를 가진 부서를 찾을 수 없음"
    ),
    DEPARTMENT_NAME_DUPLICATE(
            Level.ERROR,
            HttpStatus.CONFLICT,
            "잘못된 요청입니다.",
            "[DEPARTMENT] 이미 존재하는 부서명으로 등록/수정을 시도함"
    ),
    DEPARTMENT_HAS_EMPLOYEES(
            Level.ERROR,
            HttpStatus.CONFLICT,
            "잘못된 요청입니다.",
            "[DEPARTMENT] 소속된 직원이 있는 부서를 삭제하려고 시도함"
    ),

    // 파일 관련
    FILE_NOT_FOUND(
        Level.ERROR,
        HttpStatus.NOT_FOUND,
        "해당 파일을 찾을 수 없습니다.",
        "파일 id 값이 유효하지 않습니다."
    ),
    FILE_UPLOAD_ARGUMENT_INVALID(
        Level.ERROR,
        HttpStatus.BAD_REQUEST,
        "파일 업로드에 실패하였습니다",
        "파일 업로드에 필요한 데이터가 유효하지 않습니다."
    ),
    FILE_UPLOAD_FAILED(
        Level.ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "파일 업로드에 실패하였습니다",
        "생성파일 로컬 저장소로 업로드 중에 오류가 발생하였습니다."
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
    ),

    // 파일 생성 관련
    CSV_INIT_FAILED(
        Level.ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "백업 생성 실패",
        "백업 파일 생성에 실패하였습니다."
    );

    Level logLevel;
    HttpStatus status;
    String message;
    String details;
}
