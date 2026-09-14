package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import org.springframework.core.io.Resource;

/*
    도메인에서 응답양식으로 던져주는 객체 형태 - 컨트롤러에서 이를 받아 응답 양식 구성(dto 내부 메서드로 구현)
 */
public record FileDownload(
    Resource resource,
    String fileName,
    String contentType
) {
    public static FileDownload of(MetaFile metaFile, Resource resource){
        return new FileDownload(
            resource,
            metaFile.getFileName(),
            metaFile.getFileType()
        );
    }
}
