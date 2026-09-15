package com.sb14.hrbank.domain.service.backup;

import com.opencsv.CSVWriter;
import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.EmployeeRepository;
import com.sb14.hrbank.domain.repository.EmployeeRepository.EmployeeCsvForm;
import com.sb14.hrbank.util.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
public class CsvBackupFileGenerator {
    private final EmployeeRepository employeeRepository;
    private final FileUtils fileUtils;

    private final long CHUNK_SIZE = 5000L;
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

    public Path createCsvFile(){
        Path filePath = fileUtils.createCsvFile(FileCategory.BACKUP_CSV);
        long idAfter = 0L;
        boolean firstPage = true;

        try {
            while (true) {
                List<EmployeeCsvForm> employeeCsvForms = employeeRepository
                    .selectEmployeeInfoCsvFormPage(idAfter, CHUNK_SIZE);

                if (employeeCsvForms.isEmpty()) break;

                byte[] chunk = createCsvChunk(employeeCsvForms, firstPage);
                fileUtils.appendFile(chunk, filePath.toString());

                idAfter = employeeCsvForms.get(employeeCsvForms.size() - 1).id();
                firstPage = false;
            }

            return filePath;

        } catch (Exception e) {
            fileUtils.deleteFile(filePath.toString());
            throw e;
        }
    }

    private byte[] createCsvChunk(List<EmployeeCsvForm> employeeCsvForms, boolean firstPage){
        try(
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            CSVWriter csvWriter = new CSVWriter(writer)
        ){
            // 컬럼 명 작성
            if(firstPage) csvWriter.writeNext(columns);

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
            log.warn("===== CSV 데이터 생성 실패 =====");
            throw new HrBankException(HrBankExceptionType.CSV_INIT_FAILED, "CSV 데이터 만들때 실패했음");
        }
    }

    public Path createErrorLogFile(String worker, String errorReason){
        StringBuilder builder = new StringBuilder();
        builder.append("date = ").append(Instant.now()).append("\n");
        builder.append("worker = ").append(worker).append("\n");
        builder.append("errorReason = ").append(errorReason).append("\n");

        return fileUtils.writeFile(builder.toString().getBytes(StandardCharsets.UTF_8), FileCategory.ERROR_LOG);
    }
}
