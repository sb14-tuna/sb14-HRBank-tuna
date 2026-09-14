package com.sb14.hrbank.domain.service.file;



import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


/*
    1. JUnit이 FileServiceTest 실행 시작
    2. @SpringBootTest가 Spring 애플리케이션 전체를 먼저 실행
    3. application.yaml 디비 환경변수 읽기
    4. DB_URL이 없어서 데이터소스 생성 실패
    5. 테스트 메서드는 실행 시 에러
 */
@Slf4j
@SpringBootTest
class FileServiceImplTest {
    @Autowired
    FileServiceImpl fileServiceImpl;
    MultipartFile file = null;

    @BeforeEach
    void 파일_생성(){
        file = new MockMultipartFile(
            "file",
            "profile.png",
            "image/png",
            "test".getBytes()
        );
    }

    @Test
    void 정상_동작() {
        MetaFile metaFile = null;
        metaFile = fileServiceImpl.createFile(file, FileCategory.PROFILE_IMAGE);

        Assertions.assertThat(metaFile).isNotNull();
        Assertions.assertThat(metaFile.getCategory()).isEqualTo(FileCategory.PROFILE_IMAGE);
        log.info("==== {} ==== {}", metaFile.getFilePath(), metaFile.getFileName());
    }
}