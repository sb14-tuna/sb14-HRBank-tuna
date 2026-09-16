package com.sb14.hrbank.domain.service.department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DepartmentSearchCondition {
    String nameOrDescription;
    Long idAfter;
    String cursor;
    Integer size;
    String sortField;
    String sortDirection;

    public static DepartmentSearchCondition of(
            String nameOrDescription,
            Long idAfter,
            String cursor,
            Integer size,
            String sortField,
            String sortDirection
    ) {
        return new DepartmentSearchCondition(
                nameOrDescription,
                idAfter,
                cursor,
                size,
                sortField,
                sortDirection
        );
    }
}
