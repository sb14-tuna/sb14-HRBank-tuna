package com.sb14.hrbank.domain.service.file;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class FileGeneratorTest {
    @Autowired private FileGenerator fileGenerator;
    @Test
    void 출력_ㄱ(){
        byte[] csvFile = fileGenerator.createCsvFile();
        String result = new String(csvFile, StandardCharsets.UTF_8);

        System.out.println(result);
    }

    @Test
    void 메모리_성능췍() throws InterruptedException{
        System.out.println("프로세스 번호 이거임 ------------ >>>>> " + ProcessHandle.current().pid());
        Thread.sleep(10000);
        byte[] csvFile = fileGenerator.createCsvFile();

        Thread.sleep(60000);
    }
}