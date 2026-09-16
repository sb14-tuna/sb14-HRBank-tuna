package com.sb14.hrbank.domain.repository.department;

import com.sb14.hrbank.domain.entity.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, DepartmentQueryRepository {
    boolean existsByName(String name);
}
