package com.sb14.hrbank.domain.service.department;


import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.department.DepartmentEmployeeCount;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.department.DepartmentRepository;
import com.sb14.hrbank.web.controller.dto.CursorPageResponseDepartmentDto;
import com.sb14.hrbank.web.controller.dto.DepartmentCreateRequest;
import com.sb14.hrbank.web.controller.dto.DepartmentDto;
import com.sb14.hrbank.web.controller.dto.DepartmentUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateRequest createRequest) {
        if (departmentRepository.existsByName(createRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + createRequest.getName());
        }
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
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new HrBankException(
                        HrBankExceptionType.DEPARTMENT_NOT_FOUND,
                        departmentId.toString()
                ));
        long employeeCount = getEmployeeCount(departmentId);
        return DepartmentDto.from(department, employeeCount);
    }

    @Override
    public CursorPageResponseDepartmentDto findAll(
            DepartmentSearchCondition request
    ) {
        List<Department> searchedDepartments = departmentRepository.findAllByCondition(request);

        boolean hasNextPage = searchedDepartments.size() > request.getSize();

        if (hasNextPage) {
            searchedDepartments.remove(searchedDepartments.size() - 1);
        }


        // =======
        List<Long> departmentIds = searchedDepartments.stream()
                .map(Department::getId)
                .toList();
        // 부서별 직원수
        Map<Long, Long> employeeCountByDepartmentId =
                departmentRepository
                        .countEmployeesByDepartment(departmentIds)
                        .stream()
                        .collect(Collectors.toMap(
                                DepartmentEmployeeCount::getDepartmentId,
                                DepartmentEmployeeCount::getEmployeeCount
                        ));


        // dto로 변환..
        List<DepartmentDto> searchedDepartmentsDto =
                searchedDepartments.stream()
                        .map(department -> {
                            Long employeeCount =
                                    employeeCountByDepartmentId.get(
                                            department.getId()
                                    );

                            if (employeeCount == null) {
                                employeeCount = 0L;
                            }

                            return DepartmentDto.from(
                                    department,
                                    employeeCount
                            );
                        })
                        .toList();

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNextPage) {
            Department lastDepartmentOfPage = searchedDepartments.get(searchedDepartments.size() - 1);
            nextCursor = switch (request.getSortField()) {
                case "name" ->
                        lastDepartmentOfPage.getName();
                case "establishedDate" ->
                        lastDepartmentOfPage.getEstablishedDate().toString();
                default ->
                        throw new HrBankException(
                                HrBankExceptionType.ILLEGAL_SORT_FIELD,
                                request.getSortField()
                        );
            };
            nextIdAfter = lastDepartmentOfPage.getId();
        }
        long totalElements = departmentRepository.countByCondition(request);

        return CursorPageResponseDepartmentDto.from(
                searchedDepartmentsDto,
                nextCursor,
                nextIdAfter,
                totalElements,
                hasNextPage
        );
    }

    @Override
    @Transactional
    public DepartmentDto updateDepartment(Long departmentId, DepartmentUpdateRequest updateRequest) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new NoSuchElementException("존재 하지 않는 부서: " + departmentId));

        if (!department.getName().equals(updateRequest.getName()) && departmentRepository.existsByName(updateRequest.getName())) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다.: " + updateRequest.getName());
        }
        department.updateDepartmentInfo(updateRequest.getName(), updateRequest.getDescription(),updateRequest.getEstablishedDate());

        long employeeCount = getEmployeeCount(departmentId);
        return DepartmentDto.from(department, employeeCount);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서: " + departmentId));
        departmentRepository.delete(department);    // soft delete로 바꿔라
    }


    // ============================================================================

    private long getEmployeeCount(Long departmentId) {
        return departmentRepository
                .countEmployeesByDepartment(
                        List.of(departmentId)
                )
                .stream()
                .findFirst()
                .map(DepartmentEmployeeCount::getEmployeeCount)
                .orElse(0L);
    }
}
