package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.employee.EmployeeHireDateCount;
import com.sb14.hrbank.domain.entity.employee.EmployeeStatus;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.department.DepartmentRepository;
import com.sb14.hrbank.domain.entity.employee.EmployeeGroupCount;
import com.sb14.hrbank.domain.repository.employee.EmployeeRepository;
import com.sb14.hrbank.domain.service.file.FileService;
import com.sb14.hrbank.web.controller.dto.*;
import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeCountRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDistributionDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final FileService fileService;

    @Override
    @Transactional
    public EmployeeDto createEmployee(
            EmployeeCreateRequest createRequest,
            MultipartFile profile
    ) {
        employeeRepository.validateUniqueEmail(createRequest.getEmail());
        Department department = departmentRepository
                .findByIdOrThrow(createRequest.getDepartmentId());

        MetaFile profileImage = (Objects.nonNull(profile))
                ? fileService.createFile(profile, FileCategory.PROFILE_IMAGE)
                : null;

        Employee employee = Employee.init(
                createRequest.getName(),
                createRequest.getEmail(),
                createRequest.getPosition(),
                createRequest.getHireDate(),
                department,
                profileImage
        );

        Employee createdEmployee = employeeRepository.save(employee);

        // todo: 생성 이력 히스토리 테이블에 적재

        return EmployeeDto.from(createdEmployee);
    }

    @Override
    public EmployeeDto findById(Long employeeId) {
        Employee employee = employeeRepository.findByIdOrThrow(employeeId);
        return EmployeeDto.from(employee);
    }


    @Override
    public CursorPageResponseEmployeeDto findAll(EmployeeSearchCondition request) {
        validateSearchCondition(request);

        List<Employee> searchedEmployees =
                employeeRepository.findPageByCondition(request);
        long totalElements = employeeRepository.countByCondition(request);

        return CursorPageResponseEmployeeDto.from(
                searchedEmployees,
                request.getSize(),
                request.getSortField(),
                totalElements
        );
    }

    @Override
    public long countEmployeeByStatusAndDateRange(EmployeeCountRequest queryCountRequest) {
        return employeeRepository.countByStatusAndHireDateRange(
                queryCountRequest.getStatus(),
                queryCountRequest.getFromDate(),
                queryCountRequest.getToDate()
        );
    }

    @Override
    public List<EmployeeTrendDto> getEmployeeTrend(String unit) {
        return List.of();
    }

    @Override
    public EmployeeDto updateEmployee(
            Long employeeId,
            EmployeeUpdateRequest updateRequest,
            MultipartFile profile
    ) {
        Employee employee = employeeRepository.findByIdOrThrow(employeeId);

        // 이메일 변경 됐을 때만 중복 검사
        if (employee.checkIfEmailChanged(updateRequest.getEmail())) {
            employeeRepository.validateUniqueEmail(updateRequest.getEmail());
        }

        // 부서 Id로 실제 부서 객체 불러오기
        Department department = departmentRepository.findByIdOrThrow(updateRequest.getDepartmentId());


        MetaFile previousProfile = employee.getProfileImage();
        MetaFile newProfile;

        /* 비어있지 않은 profile 요청 */
        if (Objects.nonNull(profile)) {
            // 케이스 1: 원래 프로필 이미지 없는데 추가 될 때
            if (Objects.isNull(previousProfile)) {
                newProfile = fileService.createFile(profile, FileCategory.PROFILE_IMAGE);
            }
            // 케이스 2: 원래 프로필 이미지 있는데 교체 될 때
              else {
                fileService.deleteFile(previousProfile.getId());
                newProfile = fileService.createFile(profile, FileCategory.PROFILE_IMAGE);
            }
        }
        /* 비어있는 profile 요청 */
          else {
            // 케이스 3: 원래 프로필 이미지 있는데 삭제 하거나 (빈 profile RequestPart로)
            if (Objects.nonNull(previousProfile)) {
                fileService.deleteFile(previousProfile.getId());
                newProfile = null;
            }
            // 케이스 4: 원래 프로필 이미지 없는데 요청 들어온 것도 없을 떄
              else {
                newProfile = null;
            }
        }

        employee.update(
                updateRequest.getName(),
                updateRequest.getEmail(),
                updateRequest.getPosition(),
                updateRequest.getHireDate(),
                updateRequest.getStatus(),      // 직원 수정을 통한 퇴사 상태변경은 여기서
                department,
                newProfile
        );

        // todo: 업데이트 이력 히스토리 테이블에 적재 - employeeHistoryRepository.save(<>)

        return EmployeeDto.from(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findByIdOrThrow(employeeId);

        // todo: 업데이트 이력 히스토리 테이블에 "직원 삭제"로 적재 - employeeHistoryRepository.save(<>)

        employeeRepository.delete(employee);    // 수정
    }

    @Override
    public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status) {
        List<EmployeeGroupCount> groupCounts = employeeRepository
                .findDistribution(groupBy, status);
        return EmployeeDistributionDto.from(groupCounts);
    }

    // ============================================================================================================

    private void validateSearchCondition(EmployeeSearchCondition condition) {
        validateHireDateRange(condition);
        validateCursor(condition);
        validateCursorFormat(condition);
    }


    private void validateHireDateRange(EmployeeSearchCondition condition) {
        LocalDate from = condition.getHireDateFrom();
        LocalDate to = condition.getHireDateTo();

        if (Objects.nonNull(from)
                && Objects.nonNull(to)
                && from.isAfter(to)) {
            throw new HrBankException(
                    HrBankExceptionType.INVALID_EMPLOYEE_SEARCH_CONDITION,
                    String.format("from: %s, to: %s", from, to)
            );
        }
    }

    private void validateCursor(EmployeeSearchCondition condition) {
        if (Objects.nonNull(condition.getIdAfter())
                && !hasText(condition.getCursor())) {
            throw new HrBankException(
                    HrBankExceptionType.INVALID_EMPLOYEE_SEARCH_CONDITION,
                    String.format("idAfter: %s, cursor: %s", condition.getIdAfter(), condition.getCursor())
            );
        }
    }

    private void validateCursorFormat(EmployeeSearchCondition condition) {
        if (!hasText(condition.getCursor())
                || !"hireDate".equals(condition.getSortField())) {
            return;
        }

        try {
            LocalDate.parse(condition.getCursor());
        } catch (DateTimeParseException exception) {
            throw new HrBankException(
                    HrBankExceptionType.ILLEGAL_DATE_FORMAT,
                    "입사일 cursor는 yyyy-MM-dd 형식이어야 합니다."
            );
        }
    }

    private void validateTrendCondition(
            LocalDate from,
            LocalDate to,
            String unit
    ) {
        if (from.isAfter(to)) {
            throw new HrBankException(
                    HrBankExceptionType.INVALID_EMPLOYEE_SEARCH_CONDITION,
                    String.format("from: %s, to: %s", from, to)
            );
        }

        if (!Set.of("day", "week", "month", "quarter", "year").contains(unit)) {
            throw new HrBankException(
                    HrBankExceptionType.ILLEGAL_COUNT_UNIT,
                    unit
            );
        }
    }

}
