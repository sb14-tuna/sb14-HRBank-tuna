package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    MetaFile createFile(MultipartFile file, FileCategory fileCategory);     // 이제 안씀
    void deleteFile(Long id);
    FileDownload getFileDownload(Long id);
    MetaFile completeFile(Path filePath, FileCategory fileCategory);

    /*
        ProfileImageFileSerce ->
            ImageFileService -> fileRepository + FileUtils (멀티파트파일 인코딩 리턴 메타데이터)
        EmployeeFS -> ?
            CSVFS -> fileService + FileUtils (바이트배열 인코딩 리턴 메타데이터)

        묶을 때 전략 - 템플릿메서드 분기타는거
     */
    MetaFile save(MetaFile metaFile);
}
