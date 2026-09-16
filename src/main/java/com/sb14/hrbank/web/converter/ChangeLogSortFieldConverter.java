package com.sb14.hrbank.web.converter;

import com.sb14.hrbank.domain.entity.employeehistory.ChangeLogSortField;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ChangeLogSortFieldConverter implements Converter<String, ChangeLogSortField> {
    @Override
    public ChangeLogSortField convert(String source) {
        return switch (source.toLowerCase()) {
            case "ip_address" -> ChangeLogSortField.IP_ADDRESS;
            case "at" -> ChangeLogSortField.AT;
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다 : " + source);
        };
    }
}
