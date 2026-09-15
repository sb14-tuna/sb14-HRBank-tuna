package com.sb14.hrbank.domain.repository.department;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.department.DepartmentEmployeeCount;
import com.sb14.hrbank.domain.service.department.DepartmentSearchCondition;

import java.util.List;

public interface DepartmentQueryRepository {
    List<Department> findAllByCondition(DepartmentSearchCondition condition);
    long countByCondition(DepartmentSearchCondition condition);
    List<DepartmentEmployeeCount> countEmployeesByDepartment(List<Long> departmentIds);
}
