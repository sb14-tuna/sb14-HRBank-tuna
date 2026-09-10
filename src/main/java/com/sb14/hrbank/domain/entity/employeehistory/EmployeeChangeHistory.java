package com.sb14.hrbank.domain.entity.employeehistory;

import com.sb14.hrbank.domain.entity.employee.Employee;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "employees_change_history")
public class EmployeeChangeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_change_history_seq")
    @SequenceGenerator(
            name = "employee_change_history_seq",
            sequenceName = "employee_change_history_seq",
            allocationSize = 1
    )
    @Column(name = "employee_change_history_id")
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_change_history_type", nullable = false)
    EmployeeChangeHistoryType type;

    @Column(name = "memo")
    String memo;

    @Column(name = "ip_address", nullable = false)
    String ipAddress;

    @Column(name = "updated_at", nullable = false)
    LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    Employee employee;

    @OneToMany(
            mappedBy = "employeeChangeHistory",
            cascade = CascadeType.PERSIST
    )
    List<EmployeeChangeDetail> diffs = new ArrayList<>();

    public static EmployeeChangeHistory init(
            EmployeeChangeHistoryType type,
            String memo,
            String ipAddress,
            LocalDate updatedAt,
            Employee employee,
            List<EmployeeChangeDetail> diffs
    ) {
        return EmployeeChangeHistory.builder()
                .type(type)
                .memo(memo)
                .ipAddress(ipAddress)
                .updatedAt(updatedAt)
                .employee(employee)
                .diffs(diffs)
                .build();
    }
}
