package com.sb14.hrbank.domain.repository;

import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeChangeHistoryRepository extends JpaRepository<EmployeeChangeHistory, Long>, EmployeeChangeHistoryQueryRepository {

}
