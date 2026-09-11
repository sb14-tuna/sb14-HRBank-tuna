package com.sb14.hrbank.domain.service.employee;

import com.sb14.hrbank.domain.entity.department.Department;
import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.repository.DepartmentRepository;
import com.sb14.hrbank.domain.repository.EmployeeRepository;
import com.sb14.hrbank.domain.service.file.IFileService;
import com.sb14.hrbank.web.controller.dto.EmployeeCreateRequest;
import com.sb14.hrbank.web.controller.dto.EmployeeDto;
import com.sb14.hrbank.web.controller.dto.EmployeeUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final IFileService fileService;

    @Override
    @Transactional
    public EmployeeDto createEmployee(
            EmployeeCreateRequest createRequest,
            MultipartFile profile
    ) {
        if (employeeRepository.existsByEmail(createRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + createRequest.getEmail());
        }

        Department department = departmentRepository.findById(createRequest.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다: "+ createRequest.getDepartmentId()));

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
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원: " + employeeId));    // 404

        return EmployeeDto.from(employee);
    }

    @Override
    public EmployeeDto updateEmployee(
            Long employeeId,
            EmployeeUpdateRequest updateRequest,
            MultipartFile profile
    ) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원: " + employeeId));

        // 이메일 변경 됐을 때만 중복 검사
        if (!employee.getEmail().equals(updateRequest.getEmail())
            && employeeRepository.existsByEmail(updateRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일: " + updateRequest.getEmail());
        }

        // 부서 Id로 실제 부서 객체 불러오기
        Department department = departmentRepository.findById(updateRequest.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException( "존재하지 않는 부서: " + updateRequest.getDepartmentId()));


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
        // 그런데 - 같은 프로필 이미지를 재첨부해서 수정 누르면 중복 저장됨 흠..

        employee.update(
                updateRequest.getName(),
                updateRequest.getEmail(),
                updateRequest.getPosition(),
                updateRequest.getHireDate(),
                updateRequest.getStatus(),      // 직원 수정을 통한 퇴사 상태변경은 여기서
                department,
                newProfile
        );

        // todo: 업데이트 이력 히스토리 테이블에 적재

        return EmployeeDto.from(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 직원: " + employeeId));    // 404

        // todo: 업데이트 이력 히스토리 테이블에 "직원 삭제"로 적재

        employee.softDelete();  // EmployeeStatus.DELETED로 상태 변경
        employeeRepository.delete(employee);
    }
}
