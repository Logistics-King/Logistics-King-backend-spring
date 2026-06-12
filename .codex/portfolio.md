# 택배왕 포트폴리오 작업 정리

## 프로젝트 한 줄 요약

택배왕은 화주가 택배 계약 요청을 등록하면 여러 택배 대리점이 비공개로 단가와 서비스 조건을 제안하고, 화주가 이를 비교해 계약을 확정하는 물류 계약 플랫폼입니다.

## 문제 정의

소상공인, 쇼핑몰, 오프라인 매장 운영자는 택배 계약을 맺을 때 지역별 대리점에 직접 연락해 단가, 픽업 시간, 토요일 배송, 반품 처리, 냉장/냉동 가능 여부를 일일이 확인해야 합니다.

같은 택배사라도 대리점마다 조건이 다르기 때문에 단순히 택배사 이름만으로는 실제 계약 조건을 비교하기 어렵습니다. 택배왕은 이 과정을 계약 요청, 제안, 비교, 계약 확정 흐름으로 디지털화하는 것을 목표로 합니다.

## 내가 주로 초점을 맞춘 부분

### 1. 프로토타입에 맞는 단순한 아키텍처

처음부터 복잡한 멀티 모듈이나 과한 클린 아키텍처를 적용하지 않고 `app`, `domain`, `infra` 3계층으로 나눴습니다.

- `app`: Controller, Request/Response DTO, UseCase interface
- `domain`: 도메인 객체, 도메인 서비스, Repository interface, 비즈니스 규칙
- `infra`: JPA Entity, Repository 구현체, Redis, Security, Swagger

핵심은 단순하지만 바뀌기 쉬운 요구사항에 대응할 수 있도록 HTTP DTO, domain 객체, JPA Entity를 분리한 것입니다.

### 2. 도메인 중심 모델링

택배왕의 핵심 도메인을 다음 흐름으로 정리했습니다.

```text
화주 회원가입
-> 화주 배송 품목 등록
-> 계약 요청 등록
-> 대리점 제안 제출
-> 화주가 제안 비교
-> 제안 수락
-> 최종 계약 생성
```

주요 도메인:

- `User`: 로그인 계정과 권한의 기준
- `Vendor`: 화주
- `VendorProduct`: 화주가 보내는 배송 품목
- `Agency`: 택배 대리점
- `Driver`: 배송기사
- `ContractRequest`: 화주의 계약 요청
- `Proposal`: 대리점의 단가/조건 제안
- `Contract`: 최종 확정 계약
- `DeliverContract`: 대리점과 배송기사 간 업무 계약

도메인 객체는 JPA annotation을 모르도록 유지하고, 생성/수정 시 `requireDomain` 기반으로 도메인 불변조건을 검증하도록 구성했습니다.

### 3. 인증과 권한 기반 API 보호

권한은 실제 서비스 주체에 맞춰 분리했습니다.

```text
ADMIN
VENDOR
AGENCY
DRIVER
```

구현한 인증/권한 흐름:

- 권한별 회원가입 API
- 로그인 API
- JWT access token 발급
- HttpOnly cookie 기반 access token 전달
- Redis refresh token 저장
- refresh token 기반 재발급
- logout 시 refresh token 삭제
- `end_points` 테이블 기반 URL 권한 관리
- 신규 API 기본 `ADMIN` 권한 자동 등록

Access Token은 짧은 만료시간의 JWT로만 사용하고 Redis에 저장하지 않았습니다. Refresh Token만 Redis에 저장해 재발급과 로그아웃 제어 지점으로 사용했습니다.

### 4. API 응답과 예외 처리 일관성

프론트엔드 연동을 고려해 모든 API 응답을 `payload`로 감싸는 형태로 통일했습니다.

```json
{
  "payload": {
    "code": "SUCCESS",
    "errorMessage": null,
    "response": {}
  }
}
```

예외 처리도 `GlobalExceptionHandler`에서 관리하도록 정리했습니다.

- 도메인 예외
- 인증/권한 예외
- 잘못된 JSON 요청
- 필수 요청값 누락
- enum/type mismatch
- validation 실패

요청 형식이 잘못됐을 때 어떤 필드가 문제인지, 기대 타입이 무엇인지 프론트에서 확인할 수 있도록 에러 메시지를 구체화했습니다.

### 5. 계약 확정 동시성 제어

여러 대리점이 같은 계약 요청에 동시에 제안하고, 화주가 하나의 제안을 수락하는 구조에서는 중복 계약 생성 위험이 있습니다.

이를 막기 위해 계약 확정 흐름에서 다음 기준을 적용했습니다.

- 계약 요청 조회 시 비관락 적용
- 해당 계약 요청의 제안 목록 조회 시 비관락 적용
- 이미 확정된 계약 요청은 다시 계약 불가
- 수락된 제안 외 나머지 제안은 거절 상태로 변경
- 계약 요청과 제안에 unique 제약 추가

JPQL 문자열보다 Querydsl을 사용해 도메인 필드 변경 시 컴파일 단계에서 문제를 더 빨리 발견할 수 있도록 했습니다.

### 6. 콜드체인 조건 세분화

초기에는 냉장/냉동 조건을 Boolean으로 관리했지만, 실제 배송 조건에서는 “필요 없음”, “냉장”, “냉동”을 구분해야 합니다.

이를 위해 `ColdChainType` enum을 추가했습니다.

```kotlin
enum class ColdChainType {
    NONE,
    REFRIGERATED,
    FROZEN,
}
```

반영 범위:

- `VendorProduct`
- `Agency`
- `ContractRequest`
- `Proposal`
- `Contract`
- Request/Response DTO
- JPA Entity
- SQL DDL
- 테스트 fixture

이 변경으로 배송 조건 모델이 Boolean보다 확장 가능하고 실제 물류 요구사항에 가까운 형태가 됐습니다.

### 7. 페이지네이션과 프론트 연동 고려

프론트엔드에서 무한 스크롤보다 페이지 번호 UI를 사용할 가능성이 높다고 판단해 목록 API는 offset pagination으로 정리했습니다.

적용 대상:

- 화주 배송 품목 목록
- 계약 요청 목록
- 계약 요청별 제안 목록
- 대리점 제안 목록
- 최종 계약 목록
- 대리점 소속 배송기사 목록
- 배송기사 계약 목록

목록 응답은 `items`, `page`, `size`, `totalElements`, `totalPages`, `hasNext`, `hasPrevious` 구조로 통일했습니다.

### 8. 로컬 API 검증과 DB 검증 루틴

API 구현 후 단순히 컴파일만 확인하지 않고, Postman/curl 기준 테스트와 DB 저장 결과 확인을 함께 진행하는 루틴을 만들었습니다.

검증 관점:

- API request/response 확인
- HttpOnly cookie 인증 흐름 확인
- MySQL 저장 데이터 확인
- `end_points` 권한 데이터 확인
- pagination 응답 total count와 DB count 비교

이를 위해 `postman-local-test` 스킬을 별도로 관리했습니다.

### 9. AI 하네스 기반 개발 프로세스

이 프로젝트는 AI를 단순 코드 생성 도구가 아니라 개발 흐름을 관리하는 하네스로 사용했습니다.

정리한 규칙:

- `.codex/domain.md`: 도메인 지식
- `.codex/architecture.md`: 계층과 의존 방향
- `.codex/api.md`: API 작성 규칙
- `.codex/error-handling.md`: 예외 처리 규칙
- `.codex/persistence.md`: JPA/SQL 규칙
- `.codex/querydsl.md`: Querydsl 패턴
- `.codex/commit.md`: 커밋 분리 규칙
- `.codex/orchestration.md`: 구현 흐름과 서브 에이전트 분리 기준

활용한 스킬:

- `commit-splitter`: 요구사항 단위 커밋 분리
- `postman-local-test`: 로컬 API와 DB 검증
- `api-spec-handoff`: 프론트엔드 전달용 API spec 정리
- `work-memory`: 긴 작업의 상태 저장

## 현재까지 구현한 핵심 기능

### 인증/사용자

- UUIDv7 기반 사용자 PK
- 권한별 회원가입
- 로그인
- refresh token 재발급
- 로그아웃
- Redis refresh token 저장
- HttpOnly cookie 인증

### 권한

- `ADMIN`, `VENDOR`, `AGENCY`, `DRIVER` 권한 모델
- `end_points` 기반 URL 권한 관리
- 신규 API 자동 등록
- 기본 `ADMIN` 권한 부여

### 화주

- 화주 프로필 생성/조회/수정
- 배송 품목 CRUD
- 배송 품목 카테고리 관리
- 콜드체인 타입 세분화

### 대리점

- 대리점 프로필 생성/조회/수정
- 택배사 구분
- 서비스 지역 관리
- 토요일 집하/배송, 반품, 콜드체인 지원 조건 관리

### 배송기사

- 배송기사 프로필 생성/조회/수정
- 대리점 소속 배송기사 목록 조회
- 대리점과 배송기사 간 계약 흐름 기반 마련

### 계약 요청

- 화주 계약 요청 생성/조회/수정/취소
- OPEN 계약 요청 목록 조회
- 배송 조건, 박스 크기, 월 예상 물량, 목표 단가 관리

### 제안

- 대리점 제안 생성/조회/수정/철회
- 제안 상태 관리
- 제안 수락 시 나머지 제안 거절 처리

### 최종 계약

- 제안 수락 기반 최종 계약 생성
- 중복 계약 방지
- 화주/대리점 기준 계약 목록 조회

## 기술 스택

- Kotlin 2.2.21
- Java 21
- Spring Boot 4.0.6
- Spring Security
- Spring Data JPA
- Querydsl
- MySQL
- Redis
- Gradle
- Springdoc OpenAPI

## 검증 기준

주요 작업 후 기본 검증:

```bash
./gradlew compileKotlin
./gradlew test
```

API 작업 후 추가 검증:

```text
curl/Postman 요청
응답 payload 확인
DB 저장 결과 확인
권한 테이블 확인
프론트 전달용 API spec 정리
```

## 포트폴리오에서 강조할 수 있는 점

- 도메인 요구사항을 직접 분석해 화주, 대리점, 배송기사, 계약 요청, 제안, 계약 흐름으로 모델링했습니다.
- 프로토타입 단계에 맞춰 구조는 단순하게 유지하되 DTO, domain, JPA entity 경계는 분리했습니다.
- JWT, Redis, HttpOnly cookie를 조합해 실서비스에 가까운 인증 흐름을 구성했습니다.
- URL 권한을 DB로 관리하고 신규 API 자동 등록 구조를 추가했습니다.
- 계약 확정 시 동시성 문제를 고려해 비관락과 상태 전이를 적용했습니다.
- Querydsl을 도입해 명시 쿼리의 컴파일 안정성을 확보했습니다.
- 프론트엔드 연동을 고려해 공통 응답, validation error, pagination 응답을 일관되게 설계했습니다.
- AI 하네스, 작업 메모리, API handoff, 커밋 분리 규칙을 프로젝트 운영 방식에 포함했습니다.

## 다음에 보강할 작업

- 운영 profile과 로컬 profile 분리
- Flyway 또는 Liquibase 기반 마이그레이션 도입 검토
- 인덱스 전략 정리
- API별 상세 Swagger 설명 보강
- 권한 seed/DML 배포 전략 정리
- 프론트엔드 연동 테스트 자동화
- 테스트 데이터 fixture 정리
- Docker Compose로 MySQL/Redis 로컬 실행 환경 구성
- CI에서 compile/test 자동 실행

## 작업 기록 업데이트 규칙

큰 기능 작업이 끝나면 이 문서에 아래 항목을 추가합니다.

```md
## YYYY-MM-DD 작업명

### 문제

### 해결

### 변경 범위

### 검증

### 포트폴리오 포인트
```
