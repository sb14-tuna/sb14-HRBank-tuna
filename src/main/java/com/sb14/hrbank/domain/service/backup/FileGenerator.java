package com.sb14.hrbank.domain.service.backup;

import com.opencsv.CSVWriter;
import com.sb14.hrbank.domain.exception.HrBankException;
import com.sb14.hrbank.domain.exception.HrBankExceptionType;
import com.sb14.hrbank.domain.repository.EmployeeRepository.EmployeeCsvForm;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
    https://123okk2.tistory.com/510
 */
@Slf4j
@Component
public class FileGenerator {

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

    /*
        메모리에 올라온 인코딩데이터를 할당해제해야한다.
        현재 실제파일 저장은 UploadFile 클래스가 관리한다.
        의존관계는 FileService -> uploadFile & DB

        근데 FileService 에서 파일 생성을 위한 FileGenerator 를 호출한다.
        이게 문제인데 지금 FileGenerator 에서 완성한 csv 내용을 그대로 파일서비스로 리턴하고 파일서비스에서 UploadFile 을 호출하는 구조인데
        이때 메모리는 여전히 그대로 올라와있음
        그럼 FileGenerator 에서 바로바로 UploadFile 을 호출해야하나요

        이럼 해결은 될 것 같은데 기존 프로필 이미지 업로드 기능으로
        ImageFileService 이미지업로드 -> FileService[UploadFile + DB(파일디비)]
        CSVFileService 백업전용 -> CSVFileGenerator[데이터가져오고 저장] + FileService[UploadFile + DB(파일디비)]
        startBackup 쿼리문을 여기서 레코드를 가져오고
        -> createFile(버퍼를 인자로)
        -> FileGenerator(인자받고 인코딩하고 업로드파일호출)
        -> 나눠서 저장하는데 완료되면 디비호출?
        파일이 끝인지 확인하는 인자를 던져서 파일이 완성되면 업로드 파일 종료 및 디비 저장

        그럼 백업히스토리 상태변경은 CSVFileService

        일단 메모리 해결

        업로드 파일은 파일서비스에 의해 호출되는 의존성이 깨지고 어디서든 호출되는 패키지로 둬야하나요

        강사님 임시테이블은 어디까지 백업이 진행됐는지 확인하는 용도
        임시저장 해보려면 배치량 ->

        OOM 조심 단일쓰레드만 처리하라하고 + 유저 정보만 백업해서
        낙관 제어 비관제어하는게
     */
    public byte[] createCsvFile(List<EmployeeCsvForm> employeeCsvForms, boolean firstPage){

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
