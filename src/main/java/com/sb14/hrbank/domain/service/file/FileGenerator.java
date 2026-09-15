package com.sb14.hrbank.domain.service.file;

import com.opencsv.CSVWriter;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.employee.EmployeeRepository;
import com.sb14.hrbank.domain.repository.employee.EmployeeRepository.EmployeeCsvForm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
    https://123okk2.tistory.com/510
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FileGenerator {
    private final EmployeeRepository employeeRepository;

    private final String[] columns = {
        "ID",
        "사원번호",
        "이름",
        "이메일",
        "부서",           // 조인해야함 (부서테이블 조인)
        "직급",
        "입사일",
        "상태"
    };

    public byte[] createCsvFile(){
        List<EmployeeCsvForm> employeeCsvForms = employeeRepository.selectEmployeeInfoCsvForm();

        try(
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            CSVWriter csvWriter = new CSVWriter(writer)
        ){
            // 컬럼 명 작성
            csvWriter.writeNext(columns);

            for (EmployeeCsvForm employeeCsvForm : employeeCsvForms) {
                String[] row = {
                    employeeCsvForm.id().toString(),
                    employeeCsvForm.employeeNumber(),
                    employeeCsvForm.name(),
                    employeeCsvForm.email(),
                    employeeCsvForm.departmentName(),
                    employeeCsvForm.position(),
                    employeeCsvForm.hireDate().toString(),
                    employeeCsvForm.status()
                };

                csvWriter.writeNext(row);
            }

            csvWriter.flush();
            return outputStream.toByteArray();
        }catch (IOException e){
            log.warn("===== CSV Create failed =====");
            // 여기서 메시지를 던지면 그걸 에러 로그 파일에 작성하는거로 가면 될듯
            throw new HrBankException(HrBankExceptionType.CSV_INIT_FAILED, e.getMessage());
        }
    }


    public byte[] createErrorLogFile(String worker, String errorReason){
        StringBuilder builder = new StringBuilder();
        builder.append("date = ").append(Instant.now()).append("\n");
        builder.append("worker = ").append(worker).append("\n");
        builder.append("errorReason = ").append(errorReason).append("\n");

        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }
}
