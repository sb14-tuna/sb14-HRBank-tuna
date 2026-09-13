package com.sb14.hrbank.domain.service.backuphistory;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import com.sb14.hrbank.domain.repository.backuphistory.BackupHistoryRepository;
import com.sb14.hrbank.web.controller.dto.BackupDto;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/*
    1. 가장 최근 완료된 배치 작업 시간 이후 직원 데이터가 변경된 경우에 데이터 백업이 필요한 것으로 간주합니다. 백업이 필요 없다면 건너뜀 상태로 배치 이력을 저장하고 프로세스를 종료합니다.

    a. 필요한게 무엇인가 -> 단순 레코드 몇개만 바꼈다면? 백업해야해?
    b. 직원이 휴직 -> 퇴사 -> 휴직으로 바꼈다면 이건 변경된 것으로 봐야하나?
    c. 단순하게 직원 수정 이력을 확인해서 마지막 백업일시의 데이터 이후 추가된 데이터가 있다면 이걸 백업 필요한 것으로 간주해야할까?

    c는 제일 단순
    a는 직원 수정 이력 테이블의 where 조건으로 마지막 백업 일시 이후의 데이터 개수를 가져오면됨. 1건이라도 바뀌는걸 백업필요하다 볼까?
        만일 수정됐는데 백업이 안됐어. 근데 서버 장애 터지면 이건 복구불가. 한건도 백업 필요로 보는게 맞다.(이건 걍 요구사항 고도화라 필요없을듯)

    b는 직원수정 이력 테이블

    이외의 경우가있나?

    2. 데이터 백업 필요 시 데이터 백업 이력을 등록합니다. (사용자의 수동 백업과 자동 백업)
    백업이 필요없다 판단되면 스킵드 -> 이력을 저장
    하지만 csv 파일은 생성하지 않음

    3. 이력저장과 파일 저장을 다른 트랜잭션으로 묶어라. 파일 저장이 되면 그때 이력의 상태를 변경한다
    저장 양식은 어떻게 가는걸까. 모든 직원의 이력을? 아니면 변경된 놈만? -> 모든 놈을 csv로 저장 채택

    4. 백업 성공 시 상태 수정(상태 종료시간 파일)
    실패 시 로그 파일로 저장

    todo : 비동기구조는 ? 카프카 레빗 이건 에바고 다른 방식이 있을까

    1.그럼 지금 생각해보자. 나는 처음에 파일 서비스를 csv 프로필 이미지를 멀티파트파일로 받아서 로컬저장과 디비저장을 구분하는 서비스로 만들었어.
    근데 해보니까 파일서비스의 업로드파일(지금은 크리에이트파일로되어있음)이라는 단일 메서드만으로는 csv img 를 처리하는게 힘들 것 같고
    이미지 파일은 이미 만들어진 파일을 우리 서버에 저장하고 업로드해줘 인데 csv 는 서버 자체적으로 생성해내야해.
    이걸 동일 서비스에서 처리하는게 맞을까란 의문이 들었고?

    2. 추가로 파일히스토리서비스를 통해 파일 이력을 만들어 저장하고 csv 생성해줘로 진행하려했는데
    백업히스토리서비스보다는 백업히스토리 리포지토리단위로 두고 오케스트라 서비스를 통해 호출하는게 나은거같기도하다.

    3. 그럼 다시 백업서비스에서 백업히스토리 리포지토리 + 파일서비스(만들어진 csv 파일을 업로드해줘) 로 하면
    csv 파일이나 로그 파일 그리고 히스토리 상태변경 같은 로직을 백업서비스에서 담당하는게 맞나?
    파일서비스에서 파일업로드클래스(로커파일 저장 또는 s3저장) 를 두는걸 뭐라하지 유틸클래스?

    서비스를 두는 이유를 명확히하자.
    중간에 csv 생성 중 에러가 발생하면 이 에러를 처리해서 예외로 던질 것인지 자체적으로 해 결해서 로그파일을 만두는거로 전환할지가 필요하다

    그럼 오케스트라 서비스가 필요한가?
    만일 서비스를 나눈다면 이걸 총책임할 무언가가 필요하다. 이게 이유가 되나? 왜 총책임하지? 실행이되려면 누가 호출을 해야하고
    이 호출에 따른 결과를 보고 상호작용을 결정해야해서?

    그럼 백업서비스(백업파일생성)에서 트라이캐치를 통해 캐치 메서드를 실행하면되지않나?

    그럼 생성하는 클래스를 두고 해당 클래스에서 예외를 뿌리면 백업 서비스에서 캐치로 다른 메서드를 실행하게 하고
    여기서 히스토리 리포지토리를 호출하여 상태관리를 할수 있는데?

    그럼 백업히스토리서비스에서 시작해서 -> 백업서비스에서 끝나는데? 오케스트라가 필요없음

    결론
       백업서비스: 모든 백업 관련 요구사항의 시작점 - ( csv 생성의 성공과 실패를 책임 + 백업히스토리 crud 상태 변경 책임 + 만들어진 파일의 전송요청 )
            백업히스토리 리포지토리 ( 히스토리 crud )
            파일 생성 (csv, log) 유틸 클래스
            파일 서비스 ( 만들어진 파일 업로드 요청 )

       그럼 이게 결국 오케스트라 서비스인가

     추가
        생성이 필요없다 판단 방법(스킵)
            1. 직원 수정 이력 관리 테이블에서 마지막 수정시간을 가져온다.
            2. 그 시간을 기준으로 데이터 백업 이력을 탐색한다.
                2-1. 상태가 백업 완료됨을 필터링
                2-2. 시작시간을 기준으로 마지막 수정시간과 비교해서 레코드를 추출 (시작시간과 종료시간 사이에 수정이 발생할 수 있기에)
                2-3. 레코드가 없다면 생성이 필요하다 판단

        트랜잭션 분리
            1. 파일 생성이 실패하더라도 이력은 남아야한다
            2. 트랜잭션 구분
                2-1. 백업요청 -> 파일 생성 -> 디비에 메타파-일 저장과 실제 파일 업로드를 트랜잭션 1
                2-2. 히스토리생성 및 상태 변경 -> 트랜잭션 2
            todo : csv write 중에 실패 시 복구 프로세스 나중에 생각해보기 - 요구사항에서 그냥 삭제하라함

        요청 흐름
            1. 백업 요청이 들어온다 ( 시스템 배치프로세스 또는 관리자의 요청 )
            2. 백업 생성 메서드(트랜잭션 시작)
            3. 히스토리 서비스(새로운 트랜잭션 생성)의 생성 메서드 호출
            4. 백업 파일 생성 판단
                4-1. 생성이 불필요하다
                4-2. 생성이 필요하다
            5-1. 생성이 불필요할 경우
                - 히스토리 서비스의 상태변경 메서드(새로운 트랜잭션)를 호출한다
                - 메서드 종료
            5-2. 생성이 필요할 경우
                - 파일 생성 유틸클래스에서 생성 메서드를 호출한다  생성에 실패하면 에러로그파일을 만들래요
                    - 트라이캐치 구문 내에 작성한다
                    - 생성이 정상 동작했으면 히스토리 서비스의 상태를 종료로 변경하고 메서드 종료
                    - 생성이 비정상적으로 끝났으면 파일생성클래스에서 예외를 던지게하고 이를 잡아 상태를 실패로 변경하고 에러로그 파일 생성 로직을 호출한다,
                        - 에러로그 파일 생성도 실패하면? ㅁ?ㄹ
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BackupHistoryServiceImpl implements BackupHistoryService {
    private final BackupHistoryRepository backupHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public BackupHistory createBackupHistory(String worker) {
        BackupHistory backupHistory = BackupHistory.init(worker);
        backupHistory.startBackup();
        return backupHistoryRepository.save(backupHistory);
    }

    @Override
    public BackupHistory findLatestBackupHistoryByState(BackupState state) {
        return backupHistoryRepository
            .findTopByStateOrderByStartedAtDesc(state)
            .orElse(null);
    }
}
