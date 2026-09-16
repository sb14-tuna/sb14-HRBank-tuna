package com.sb14.hrbank.web.controller.dto;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.department.DepartmentWithEmployeeCount;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CursorPageResponseDepartmentDto {
    List<DepartmentDto> content;
    String nextCursor;
    Long nextIdAfter;
    Integer size;
    Long totalElements;
    Boolean hasNext;

    public static CursorPageResponseDepartmentDto from(
            List<DepartmentWithEmployeeCount> searchedDepartments,
            int requestedSize,
            String sortField,
            long totalElements
    ) {
        // dto 조합에 필요한 연산은 여기서 수행
        // * nextCursor, nextIdAfter 계산 등
        boolean hasNext = searchedDepartments.size() > requestedSize;
        List<DepartmentWithEmployeeCount> content = hasNext
                ? searchedDepartments.subList(0, requestedSize)
                : searchedDepartments;

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext) {
            Department lastDepartment = content
                    .get(content.size() - 1)
                    .getDepartment();

            nextCursor = getCursor(lastDepartment,sortField);
            nextIdAfter = lastDepartment.getId();
        }

        List<DepartmentDto> finalContent = content.stream()
                        .map(item -> DepartmentDto.from(
                                item.getDepartment(),
                                item.getEmployeeCount()
                        ))
                        .toList();

        return new CursorPageResponseDepartmentDto(
                finalContent,
                nextCursor,
                nextIdAfter,
                content.size(),
                totalElements,
                hasNext
        );
    }

    // 부서 + 정렬 필드에 대한 커서값 반환
    private static String getCursor(Department department, String sortField) {
        return switch (sortField) {
            case "name" -> department.getName();
            case "establishedDate" -> department.getEstablishedDate().toString();
            default ->
                    throw new HrBankException(
                            HrBankExceptionType.ILLEGAL_SORT_FIELD,
                            sortField
                    );
        };
    }
}
