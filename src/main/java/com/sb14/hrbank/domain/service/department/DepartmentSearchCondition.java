package com.sb14.hrbank.domain.service.department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DepartmentSearchCondition {
    String nameOrDescription;
    Long idAfter;
    String  cursor;
    Integer size;
    String sortField;
    String sortDirection;
}
