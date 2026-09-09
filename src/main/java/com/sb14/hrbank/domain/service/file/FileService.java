package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.entity.metafile.FileCategory;
import com.sb14.hrbank.domain.entity.metafile.MetaFile;
import com.sb14.hrbank.domain.repository.file.FileRepository;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/*
    파일 서비스를 작업 중에 이걸 독립 서비스로 두는게맞나 싶음
    이 요구사항은 일단 메인주체가 아닌 서브 주체로 호출되고 저장 api(직원, 백업로그)를 통해 호출되는데 단독 서비스가 아니라 생각해서
    추가로 서비스를 두면 서비스에서 서비스를 호출하는게 도메인서비스가 아닌 유효 비즈니스 서비스를 호출한다는게 아닌거같아서 파일 저장 유틸 클래스로 변경

    근데 다시 또 생각해보면 서비스가 독립 api 를 담당하는게 아니여도 파일 저장 실페에 따른 처리를 담당해야하기에 서비스를 두는게 맞는거같음
 */
@RequiredArgsConstructor
@Service
public class FileService implements IFileService{
    private final FileRepository fileRepository;
    private final FileUpload fileUpload;

    /*
        타 서비스에서 호출로 실행됨
        - 직원 등록 수정 요구사항에서 호출
        - 백업 서비스에서 호출

        - {메타 정보}는 데이터베이스에, {실제 파일}은 로컬 디스크에 저장합니다.
     */
    @Transactional
    @Override
    public MetaFile createFile(MultipartFile file, FileCategory fileCategory) {
        MetaFile metaFile = null;

        try{
            metaFile = fileUpload.uploadFile(file, fileCategory);
            return fileRepository.save(metaFile);
        }catch (DataAccessException e){
            if(Objects.nonNull(metaFile)){
                fileUpload.deleteFile(metaFile.getFilePath());
            }

            throw new RuntimeException("DB STORE FAILED");
        }
    }
}
