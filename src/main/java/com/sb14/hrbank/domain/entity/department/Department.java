package com.sb14.hrbank.domain.entity.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "departments")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class Department {
    @Id
    @Column(name = "department_id")
    Long id;

    String name;
}
