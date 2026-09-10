package com.sb14.hrbank.domain.repository;

import com.sb14.hrbank.domain.entity.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDepartmentRepository extends JpaRepository<Department, Long> {
}
