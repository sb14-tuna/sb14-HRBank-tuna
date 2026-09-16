# HR Bank

인적 자원 관리와 변경 이력, 파일 및 백업 기능을 제공하는 HR 관리 시스템입니다.

## 프로젝트 소개

Spring Boot 기반으로 구현한 인적 자원 관리 시스템입니다.

직원 및 부서 관리, 직원 변경 이력 조회, 프로필 이미지 관리,
CSV 백업 및 백업 이력 관리 기능을 제공합니다.

- 직원 CRUD 및 검색
- 부서 CRUD
- 직원 변경 이력 관리
- 프로필 이미지 업로드/다운로드
- CSV 백업 및 백업 이력 관리
- 커서 기반 페이지네이션
- 예외 처리 및 요청 검증

---

## 주요 기능

### 1. 직원 관리

직원 등록, 수정, 삭제, 상세 조회 및 목록 조회 기능을 제공합니다.

검색 조건:
- 이름 / 이메일
- 사원번호
- 부서
- 직급
- 입사일
- 상태

커서 기반 페이지네이션을 적용하여 목록을 조회할 수 있습니다.

### 2. 부서 관리

부서 등록, 수정, 삭제 및 목록 조회 기능을 제공합니다.

소속 직원이 존재하는 부서는 삭제할 수 없도록 비즈니스 규칙을 적용했습니다.

### 3. 직원 변경 이력

직원의 생성, 수정, 삭제 내역을 기록하고 조회합니다.

변경 시점, 요청자 IP, 변경 내용 등을 관리합니다.

### 4. 파일 관리

직원 프로필 이미지를 저장하고 다운로드할 수 있습니다.

파일의 실제 저장 정보와 메타데이터를 분리해 관리합니다.

### 5. CSV 백업

전체 직원 데이터를 CSV 파일로 백업합니다.

백업 상태는 다음과 같이 관리합니다.

- PROCESSING
- COMPLETED
- FAILED
- SKIPPED

백업 실패 시 오류 로그 파일을 생성하고 백업 이력과 연결합니다.

---

## 기술 스택

### Backend

- Java 17
- Spring Boot 4
- Spring Data JPA
- QueryDSL
- Hibernate Validator
- PostgreSQL
- OpenCSV

### Infra

- Railway
- Vercel

### Test

- JUnit5
- Spring Boot Test

### Collaboration

- GitHub
- Discord
- Notion / Jira (실제로 쓴 것만)

---

## ERD

<!-- ERD 이미지 -->

![ERD](이미지 링크)

---

## 시스템 구조

```text
Client
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
PostgreSQL

File Service
  ↓
Local File Storage
