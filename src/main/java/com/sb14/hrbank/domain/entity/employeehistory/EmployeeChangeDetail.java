package com.sb14.hrbank.domain.entity.employeehistory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "employees_change_detail")
public class EmployeeChangeDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_change_detail_seq")
    @SequenceGenerator(
            name = "employee_change_detail_seq",
            sequenceName = "employee_change_detail_seq",
            allocationSize = 1
    )
    Long id;

    @Column(name = "property_name", nullable = false)
    String propertyName;

    @Column(name = "before_value")
    String beforeValue;

    @Column(name = "after_value")
    String afterValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_change_history_id", nullable = false)
    EmployeeChangeHistory employeeChangeHistory;

    public static EmployeeChangeDetail init(
            String propertyName,
            String beforeValue,
            String afterValue,
            EmployeeChangeHistory employeeChangeHistory
    ) {
        return EmployeeChangeDetail.builder()
                .propertyName(propertyName)
                .beforeValue(beforeValue)
                .afterValue(afterValue)
                .employeeChangeHistory(employeeChangeHistory)
                .build();
    }
}
