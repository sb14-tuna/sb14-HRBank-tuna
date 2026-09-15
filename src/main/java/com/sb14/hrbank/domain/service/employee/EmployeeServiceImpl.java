package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.employee.Employee;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

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
        if (employeeRepository.existsByEmail(createRequest.getEmail())) {
            throw new HrBankException(
                    HrBankExceptionType.EMAIL_ALREADY_EXISTS,
                    createRequest.getEmail()
            );
        }

        Department department = departmentRepository.findById(createRequest.getDepartmentId())
                .orElseThrow(() -> new HrBankException(
                            HrBankExceptionType.DEPARTMENT_NOT_FOUND,
                            createRequest.getDepartmentId().toString()
                        )
                );

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
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HrBankException(
                        HrBankExceptionType.EMPLOYEE_NOT_FOUND,
                        employeeId.toString())
                );

        return EmployeeDto.from(employee);
    }


    @Override
    public CursorPageResponseEmployeeDto findAll(EmployeeSearchCondition request) {
        List<Employee> searchedEmployees =
                employeeRepository.findAllByCondition(request);

        boolean hasNextPage = searchedEmployees.size() > request.getSize();

        if (hasNextPage) {
            searchedEmployees.remove(searchedEmployees.size() - 1);
        }

        // dto로 변환
        List<EmployeeDto> searchedEmployeesDto = searchedEmployees.stream()
                .map(EmployeeDto::from)
                .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNextPage) {
            Employee lastEmployeeOfPage = searchedEmployees.get(searchedEmployees.size() - 1);
            nextCursor = switch (request.getSortField()) {
                case "name" ->
                    lastEmployeeOfPage.getName();
                case "employeeNumber" ->
                    lastEmployeeOfPage.getEmployeeNumber();
                case "hireDate" ->
                    lastEmployeeOfPage.getHireDate().toString();
                default ->
                    throw new IllegalArgumentException("정렬 필드가 아님");
            };
            nextIdAfter = lastEmployeeOfPage.getId();
        }

        long totalElements = employeeRepository.countByCondition(request);

        return CursorPageResponseEmployeeDto.from(
                searchedEmployeesDto,
                nextCursor,
                nextIdAfter,
                totalElements,
                hasNextPage
        );
    }

    @Override
    public long countEmployeeByStatusAndDateRange(EmployeeCountRequest queryCountRequest) {
        return employeeRepository.countEmployeesByStatusAndDateRange(
                queryCountRequest.getStatus(),
                queryCountRequest.getFromDate(),
                queryCountRequest.getToDate()
        );
    }

    @Override
    public EmployeeDto updateEmployee(
            Long employeeId,
            EmployeeUpdateRequest updateRequest,
            MultipartFile profile
    ) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HrBankException(
                        HrBankExceptionType.EMPLOYEE_NOT_FOUND,
                        employeeId.toString())
                );

        // 이메일 변경 됐을 때만 중복 검사
        if (!employee.getEmail().equals(updateRequest.getEmail())
            && employeeRepository.existsByEmail(updateRequest.getEmail())) {
            throw new HrBankException(
                    HrBankExceptionType.EMAIL_ALREADY_EXISTS,
                    updateRequest.getEmail()
            );
        }

        // 부서 Id로 실제 부서 객체 불러오기
        Department department = departmentRepository.findById(updateRequest.getDepartmentId())
                .orElseThrow(() -> new HrBankException(
                        HrBankExceptionType.DEPARTMENT_NOT_FOUND,
                        updateRequest.getDepartmentId().toString()
                ));


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
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HrBankException(
                        HrBankExceptionType.EMPLOYEE_NOT_FOUND,
                        employeeId.toString())
                );

        // todo: 업데이트 이력 히스토리 테이블에 "직원 삭제"로 적재 - employeeHistoryRepository.save(<>)

        employeeRepository.delete(employee);    // 수정
    }

    @Override
    public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status) {
        List<EmployeeGroupCount> groupCounts = employeeRepository
                .findByEmployeeDistribution(
                    groupBy,
                    status
                );
        long employeeCount = groupCounts.stream()
                .mapToLong(EmployeeGroupCount::getCount)
                .sum();
        if (employeeCount == 0) return List.of();

        return groupCounts.stream()
                .map(groupCount -> {
                    double percentage = Math.round(groupCount.getCount() * 1000.0 / employeeCount) / 10.0;
                    return EmployeeDistributionDto.of(
                            groupCount.getGroupKey(),
                            groupCount.getCount(),
                            percentage
                    );
                })
                .toList();
    }
}
