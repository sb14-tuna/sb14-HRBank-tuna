package com.sb14.hrbank.domain.repository.department;

import com.sb14.hrbank.domain.entity.department.DepartmentWithEmployeeCount;
import com.sb14.hrbank.domain.service.department.DepartmentSearchCondition;

import java.util.List;
import java.util.Optional;

public interface DepartmentQueryRepository {
    // 검색 조건 주면: 조건 맞는 List<부서 + 부서 직원 수> 반환
    List<DepartmentWithEmployeeCount> findPageByCondition(DepartmentSearchCondition condition);

    // Department id 주면: Optional<부서 + 부서 직원 수> 반환
    Optional<DepartmentWithEmployeeCount> findByIdWithEmployeeCount(Long departmentId);

    // 검색 조건 주면: 조건 맞는 직원 수 반환
    long countByCondition(DepartmentSearchCondition condition);
}
