package com.sb14.hrbank.domain.entity.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeGroupCount {

    String groupKey;
    long count;

    public static EmployeeGroupCount of(String groupKey, long count) {
        return new EmployeeGroupCount(groupKey, count);
    }
}