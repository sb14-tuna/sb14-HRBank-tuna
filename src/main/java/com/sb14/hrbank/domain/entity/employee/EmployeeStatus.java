package com.sb14.hrbank.domain.entity.employee;

public enum EmployeeStatus {
    ACTIVE,     // 재직중
    ON_LEAVE,   // 휴직중
    RESIGNED,   // 퇴사
    DELETED     // 직원 삭제 - 추가한 이유: 삭제한 직원은 직원 테이블에서 안 보이는데, 삭제=퇴사라고 하기에는 직원 테이블에 퇴사한 직원도 보여서 새 status를 만들어봄
}
