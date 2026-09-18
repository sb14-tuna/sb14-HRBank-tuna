package com.sb14.hrbank.domain.service.department;


import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.department.DepartmentWithEmployeeCount;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.department.DepartmentRepository;
import com.sb14.hrbank.domain.repository.employee.EmployeeRepository;
import com.sb14.hrbank.web.controller.dto.CursorPageResponseDepartmentDto;
import com.sb14.hrbank.web.controller.dto.DepartmentCreateRequest;
import com.sb14.hrbank.web.controller.dto.DepartmentDto;
import com.sb14.hrbank.web.controller.dto.DepartmentUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateRequest createRequest) {
        departmentRepository.validateUniqueName(createRequest.getName());

        Department department = Department.init(
                createRequest.getName(),
                createRequest.getDescription(),
                createRequest.getEstablishedDate()
        );
        Department createdDepartment = departmentRepository.save(department);
        return DepartmentDto.from(createdDepartment);
    }


    @Override
    public DepartmentDto findDepartmentById(Long departmentId) {
        DepartmentWithEmployeeCount result = departmentRepository
                .findByIdWithEmployeeCountOrThrow(departmentId);

        return DepartmentDto.from(
                result.getDepartment(),
                result.getEmployeeCount()
        );
    }

    @Override
    public CursorPageResponseDepartmentDto findAll(
            DepartmentSearchCondition condition
    ) {
        validateSearchCondition(condition);

        List<DepartmentWithEmployeeCount> departments = departmentRepository
                .findPageByCondition(condition);

        long totalElements = departmentRepository.countByCondition(condition);

        return CursorPageResponseDepartmentDto.from(
                departments,
                condition.getSize(),
                condition.getSortField(),
                totalElements
        );
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest updateRequest) {
        DepartmentWithEmployeeCount result = departmentRepository
                .findByIdWithEmployeeCountOrThrow(departmentId);
        Department department = result.getDepartment();

        departmentRepository.validateUniqueName(updateRequest.getName());

        department.updateDepartmentInfo(
                updateRequest.getName(),
                updateRequest.getDescription(),
                updateRequest.getEstablishedDate()
        );
        return DepartmentDto.from(
                department,
                result.getEmployeeCount()
        );
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findByIdOrThrow(departmentId);
        if (employeeRepository.existsByDepartmentId(departmentId)){
            throw new HrBankException(HrBankExceptionType.DEPARTMENT_HAS_EMPLOYEES, "a?F");
        }
        departmentRepository.delete(department);    // soft delete로 바꿔라
    }

    // ============================================================================================================

    private void validateSearchCondition(DepartmentSearchCondition condition) {
        validateCursor(condition);
        validateCursorFormat(condition);
    }

    private void validateCursor(DepartmentSearchCondition condition) {
        if (Objects.nonNull(condition.getIdAfter())
                && !hasText(condition.getCursor())) {
            throw new HrBankException(
                    HrBankExceptionType.INVALID_DEPARTMENT_SEARCH_CONDITION,
                    String.format(
                            "idAfter: %s, cursor: %s",
                            condition.getIdAfter(),
                            condition.getCursor()
                    )
            );
        }
    }

    private void validateCursorFormat(DepartmentSearchCondition condition) {
        if (!hasText(condition.getCursor())
                || !"establishedDate".equals(condition.getSortField())) {
            return;
        }

        try {
            LocalDate.parse(condition.getCursor());
        } catch (DateTimeParseException exception) {
            throw new HrBankException(
                    HrBankExceptionType.ILLEGAL_DATE_FORMAT,
                    "설립일 cursor는 yyyy-MM-dd 형식이어야 합니다."
            );
        }
    }
}
