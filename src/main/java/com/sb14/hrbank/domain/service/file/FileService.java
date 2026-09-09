package com.sb14.hrbank.domain.service.file;

import com.sb14.hrbank.domain.repository.file.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
    파일 서비스를 작업 중에 이걸 독립 서비스로 두는게맞나 싶음
    이 요구사항은 일단 메인주체가 아닌 서브 주체로 호출되고 저장 api(직원, 백업로그)를 통해 호출되는데 단독 서비스가 아니라 생각해서
    추가로 서비스를 두면 서비스에서 서비스를 호출하는게 도메인서비스가 아닌 유효 비즈니스 서비스를 호출한다는게 아닌거같아서 파일 저장 유틸 클래스로 변경

    근데 다시 또 생각해보면 서비스가 독립 api 를 담당하는게 아니여도 파일 저장 실페에 따른 처리를 담당해야하기에 서비스를 두는게 맞는거같음
 */
@RequiredArgsConstructor
@Service
public class FileService {
    private final FileRepository fileRepository;


}
