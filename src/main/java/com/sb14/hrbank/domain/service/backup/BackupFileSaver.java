package com.sb14.hrbank.domain.service.backup;

import com.opencsv.CSVWriter;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.util.FileUtils;
import com.sb14.hrbank.domain.repository.employee.EmployeeRepository;
import com.sb14.hrbank.domain.repository.employee.EmployeeRepository.EmployeeCsvForm;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
public class BackupFileSaver {
    private final EmployeeRepository employeeRepository;
    private final FileUtils fileUtils;

    private final long CHUNK_SIZE = 5000L;
    private final String[] COLUMNS = {
        "ID",
        "사원번호",
        "이름",
        "이메일",
        "부서",           // 조인해야함 (부서테이블 조인)
        "직급",
        "입사일",
        "상태"
    };

    public synchronized Path saveCsvFile(){
        Path filePath = fileUtils.createCsvFile(FileCategory.BACKUP_CSV);
        try{
            writeCsv(filePath);
            return filePath;
        } catch (Exception e) {
            fileUtils.deleteFile(filePath.toString());
            throw e;
        }
    }

    private void writeCsv(Path filePath){
        long idAfter = 0L;
        try(
            CSVWriter csvWriter = new CSVWriter(
                Files.newBufferedWriter(
                    filePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
                )
            )
        ){
            csvWriter.writeNext(COLUMNS);

            while (true) {
                List<EmployeeCsvForm> employeeCsvForms = employeeRepository
                    .selectEmployeeInfoCsvFormPage(idAfter, CHUNK_SIZE);

                if (employeeCsvForms.isEmpty()) break;

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

                idAfter = employeeCsvForms.get(employeeCsvForms.size() - 1).id();
            }

            csvWriter.flush();
        }catch (IOException e){
            log.warn("===== CSV 데이터 생성 실패 =====");
            throw new HrBankException(HrBankExceptionType.CSV_INIT_FAILED,
                "CSV 데이터 만들때 실패했음"
            );
        }
    }

    public Path saveErrorLogFile(String worker, String errorReason){
        StringBuilder builder = new StringBuilder();
        builder.append("date = ").append(Instant.now()).append("\n");
        builder.append("worker = ").append(worker).append("\n");
        builder.append("errorReason = ").append(errorReason).append("\n");

        return fileUtils.writeFile(builder.toString().getBytes(StandardCharsets.UTF_8), FileCategory.ERROR_LOG);
    }
}
