package com.sb14.hrbank.web.converter;

import com.sb14.hrbank.domain.entity.employeehistory.ChangeLogSortField;
import org.hibernate.query.SortDirection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SortDirectionConverter implements Converter<String, SortDirection> {
    @Override
    public SortDirection convert(String source) {
        return switch (source.toLowerCase()) {
            case "asc" -> SortDirection.ASCENDING;
            case "desc" -> SortDirection.DESCENDING;
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 방향입니다 : " + source);
        };
    }
}
