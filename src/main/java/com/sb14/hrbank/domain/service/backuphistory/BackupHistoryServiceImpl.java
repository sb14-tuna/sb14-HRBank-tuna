package com.sb14.hrbank.domain.service.backuphistory;

import com.sb14.hrbank.domain.entity.backuphistory.BackupHistory;
import com.sb14.hrbank.domain.entity.backuphistory.BackupState;
import com.sb14.hrbank.domain.entity.employee.Employee;
import com.sb14.hrbank.domain.repository.backuphistory.BackupHistoryQueryRepository;
import com.sb14.hrbank.domain.repository.backuphistory.BackupHistoryRepository;
import com.sb14.hrbank.web.controller.dto.BackupDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/*
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
    private final BackupHistoryQueryRepository backupHistoryQueryRepository;

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

    @Override
    public BackupHistoryInfo findAllBackupHistoryByCondition(BackupSearchCondition condition) {
        List<BackupHistory> backupList = backupHistoryQueryRepository.findAllBackupHistoryByCondition(condition);
        long totalElements = backupHistoryQueryRepository.countByCondition(condition);

        return createBackupHistoryInfo(backupList, totalElements, condition);
    }

    private BackupHistoryInfo createBackupHistoryInfo(
        List<BackupHistory> backupList,
        long totalElements,
        BackupSearchCondition condition
    ){
        int size = condition.size();

        String nextCursor = null;
        Long nextIdAfter = null;
        boolean hasNext = backupList.size() > condition.size();
        if (hasNext) {
            backupList.remove(backupList.size() - 1);
            BackupHistory lastBackupHistory = backupList.get(backupList.size() - 1);

            nextIdAfter = lastBackupHistory.getId();
            nextCursor = switch (condition.sortField()){
                case "startedAt" -> lastBackupHistory.getStartedAt().toString();
                case "endedAt" -> lastBackupHistory.getEndedAt().toString();
                case "status" -> lastBackupHistory.getState().name();
                default -> throw new IllegalArgumentException("정렬 필드가 아님");
            };
        }

        return new BackupHistoryInfo(
            List.copyOf(backupList),
            nextCursor,
            nextIdAfter,
            size,
            totalElements,
            hasNext);
        }
}
