package com.sb14.hrbank.domain.repository.department;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.department.DepartmentWithEmployeeCount;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, DepartmentQueryRepository {
    boolean existsByName(String name);

    // 부서 이름 이미 존재하면 예외 발생
    default void validateUniqueName(String name) {
        if (existsByName(name)) {
            throw new HrBankException(
                    HrBankExceptionType.DEPARTMENT_ALREADY_EXISTS,
                    name
            );
        }
    }

    // 부서 존재하지 않으면 예외 발생 - Department만 반환
    default Department findByIdOrThrow(Long departmentId) {
        return findById(departmentId)
                .orElseThrow(() -> new HrBankException(
                                HrBankExceptionType.DEPARTMENT_NOT_FOUND,
                                departmentId.toString()
                        )
                );
    }

    // 부서 존재하지 않은면 예외 발생 - Department + 속해있는 직원 수 같이 반환
    default DepartmentWithEmployeeCount findByIdWithEmployeeCountOrThrow(Long departmentId) {
        return findByIdWithEmployeeCount(departmentId)
                .orElseThrow(() -> new HrBankException(
                        HrBankExceptionType.DEPARTMENT_NOT_FOUND,
                        String.valueOf(departmentId)
                ));
    }
    // 중복되나
}
