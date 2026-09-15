package com.sb14.hrbank.domain.repository.employeehistory;

import com.sb14.hrbank.domain.entity.employeehistory.EmployeeChangeHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeChangeHistory, Long> {
    Optional<EmployeeChangeHistory> findTopByOrderByUpdatedAtDesc();
}
