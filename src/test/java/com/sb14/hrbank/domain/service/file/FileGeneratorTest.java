package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.service.backup.BackupFileSaver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class FileGeneratorTest {
    @Autowired private BackupFileSaver backupFileSaver;
}