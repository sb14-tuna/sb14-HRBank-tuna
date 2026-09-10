package com.sb14.hrbank.web.controller;

import com.sb14.hrbank.domain.service.file.FileDownload;
import com.sb14.hrbank.domain.service.file.IFileService;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class FileApiController {
    private final IFileService fileService;

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downLoadFile(@NotNull @PathVariable Long id){
        FileDownload fileDownload = fileService.getFileDownload(id);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(fileDownload.contentType()))
            .header(                    // todo : 프론트에서 해당 파일을 어떻게 다룰지 설정해줘야한다함. 나중에 인코딩 방식 공부
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.inline()
                    .filename(fileDownload.fileName(), StandardCharsets.UTF_8)
                    .build()
                    .toString()
            )
            .body(fileDownload.resource());
    }
}
