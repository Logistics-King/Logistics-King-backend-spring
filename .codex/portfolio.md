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
-> 주요 상태 변경 알림
```

주요 도메인:

- `User`: 로그인 계정과 권한의 기준
- `Vendor`: 화주
- `Product`: 화주가 보내는 배송 품목
- `Agency`: 택배 대리점
- `Driver`: 배송기사
- `ContractRequest`: 화주와 대리점 사이의 계약 요청/오퍼
- `Proposal`: 대리점의 단가/조건 제안
- `Contract`: 최종 확정 계약
- `DeliverContract`: 대리점과 배송기사 간 업무 계약
- `Notification`: 계약/제안/기사 계약 상태 변경 알림

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
- `@EndpointAccess` custom annotation 기반 API 권한 선언
- 애플리케이션 시작 시 annotation 정보를 스캔해 신규 API만 `end_points` 테이블에 기본 등록
- 신규 보호 API 기본 `ADMIN` 권한 자동 등록
- `end_points` 기반 운영 권한 정책 관리
- 요청 시점에는 DB가 아니라 메모리 캐시로 URL/method/role 검사
- 운영 중 권한 변경을 위한 dry-run/reload 관리자 API

Access Token은 짧은 만료시간의 JWT로만 사용하고 Redis에 저장하지 않았습니다. Refresh Token만 Redis에 저장해 재발급과 로그아웃 제어 지점으로 사용했습니다.

처음에는 권한 정책을 DB에서 직접 조회하도록 구현했습니다. 이 방식은 DB 값을 수정하면 바로 권한 변경이 반영된다는 장점이 있었지만, 모든 보호 API 요청마다 `end_points` 전체 조회가 발생하는 문제가 있었습니다.

로컬 성능 테스트 결과 `5000/c100` 조건에서 메모리 캐시 적용 후 RPS가 `1,342.82`에서 `2,583.01`로 증가했고, 평균 지연 시간은 `74ms`에서 `39ms`로 감소했습니다. 운영 중 권한 변경 가능성은 유지하기 위해 `GET /api/v1/admin/end-points/cache/dry-run`으로 DB와 캐시 차이를 먼저 확인하고, `POST /api/v1/admin/end-points/cache/reload`로 서버 재시작 없이 캐시를 갱신하는 구조로 정리했습니다.

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

- `Product`
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
- `@EndpointAccess` custom annotation 기반 보호 API 권한 선언
- `end_points` 기반 URL/method/role 권한 정책 저장
- 애플리케이션 시작 시 신규 API 자동 등록
- 기본 `ADMIN` 권한 부여
- 메모리 캐시 기반 권한 검사
- 권한 캐시 dry-run/reload 운영 API

### 화주

- 화주 프로필 생성/조회/수정
- 배송 품목 CRUD
- 배송 품목 카테고리 관리
- 콜드체인 타입 세분화
- 배송 품목 목적지 주소 관리
- 물품명, 카테고리, 박스 사이즈, 콜드체인 조건 기반 배송 품목 검색/필터

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

- 화주 또는 대리점 계약 요청 생성/조회/수정/취소
- `VENDOR_OFFER`, `AGENCY_OFFER` 타입 기반 양방향 계약 요청
- 요청자/승인자 타입과 식별자 관리
- 받은 계약 요청 목록 조회
- 받은 계약 요청 수락/거절
- OPEN 계약 요청 목록 조회
- 배송 조건, 박스 사이즈, 월 예상 물량, 목표 단가 관리
- 박스 사이즈 enum 기반 계약 조건 관리

### 제안

- 대리점 제안 생성/조회/수정/철회
- 제안 상태 관리
- 제안 수락 시 나머지 제안 거절 처리

### 최종 계약

- 제안 수락 기반 최종 계약 생성
- 계약 요청 직접 수락 기반 최종 계약 생성
- 중복 계약 방지
- 화주/대리점 기준 계약 목록 조회

### 알림

- 계약 요청, 제안, 최종 계약, 배송기사 계약 상태 변경 알림
- 최근 30일 알림 목록 조회
- 읽지 않은 알림 수 조회
- 단건 읽음 처리
- 전체 읽음 처리
- MySQL 기반 알림 이력 저장

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
- `@EndpointAccess` custom annotation으로 신규 API의 기본 권한을 코드 가까이에 선언하고, 운영 권한 정책은 DB를 기준으로 유지하는 구조를 만들었습니다.
- 요청마다 DB를 조회하던 권한 검사 병목을 성능 테스트로 확인하고, 메모리 캐시와 dry-run/reload 운영 API로 개선했습니다.
- 계약 확정 시 동시성 문제를 고려해 비관락과 상태 전이를 적용했습니다.
- Querydsl을 도입해 명시 쿼리의 컴파일 안정성을 확보했습니다.
- 배송 품목 검색/필터에 Querydsl 동적 조건을 적용해 목록 조회 확장성을 확보했습니다.
- 박스 사이즈를 문자열에서 enum으로 전환해 단가 산정과 필터 기준을 더 안정적으로 관리할 수 있게 했습니다.
- 프론트엔드 연동을 고려해 공통 응답, validation error, pagination 응답을 일관되게 설계했습니다.
- AI 하네스, 작업 메모리, API handoff, 커밋 분리 규칙을 프로젝트 운영 방식에 포함했습니다.

## 2026-06-13 배송 품목 목적지와 검색 필터

### 문제

화주 배송 품목에는 물품명, 카테고리, 무게, 박스 크기 같은 물품 정보는 있었지만 실제 배송 목적지 정보가 없었습니다. 또한 품목 목록이 많아질 경우 물품명, 카테고리, 박스 사이즈 기준으로 원하는 품목을 찾기 어려웠습니다.

박스 사이즈도 문자열로 관리되고 있어 `60`, `60사이즈`, `SIZE_60`처럼 입력 표현이 흔들릴 수 있었습니다.

### 해결

배송 품목에 목적지 주소 정보를 추가하고, 박스 사이즈를 `BoxSize` enum으로 전환했습니다.

```kotlin
enum class BoxSize(
    val label: String,
    val maxTotalLengthCm: Int?,
) {
    SIZE_60("60사이즈", 60),
    SIZE_80("80사이즈", 80),
    SIZE_100("100사이즈", 100),
    SIZE_120("120사이즈", 120),
    SIZE_140("140사이즈", 140),
    SIZE_160("160사이즈", 160),
    ETC("기타", null),
}
```

배송 품목 목록 조회에는 검색 조건 객체를 추가했습니다.

```kotlin
data class ProductSearchCondition(
    val name: String?,
    val category: ProductCategory?,
    val boxSize: BoxSize?,
    val coldChainType: ColdChainType?,
)
```

목록 조회는 Querydsl 기반 동적 조건으로 변경했습니다.

```text
GET /api/v1/vendors/me/products?name=의류&category=CLOTHING&boxSize=SIZE_60&coldChainType=NONE&page=0&size=20
```

### 변경 범위

- `BoxSize` enum 추가
- `Product`에 목적지 주소 필드 추가
- `Product.boxSize`, `ContractRequest.boxSize`, `Contract.boxSize`를 enum으로 변경
- `ProductSearchCondition` 추가
- 화주 배송 품목 목록 API에 `name`, `category`, `boxSize`, `coldChainType` query parameter 추가
- Spring Data 메서드 기반 목록 조회를 Querydsl 동적 조건 조회로 변경
- `products` DDL에 목적지 주소 컬럼 추가
- 관련 DTO, JPA Entity, 테스트 fixture 갱신

### 검증

```bash
./gradlew test
```

### 포트폴리오 포인트

- 단순 문자열로 관리되던 박스 크기를 enum으로 전환해 API 입력값과 DB 저장값의 일관성을 높였습니다.
- 배송 품목 조회에 Querydsl 동적 필터를 적용해 검색 조건이 늘어나도 repository 메서드명이 복잡해지지 않도록 설계했습니다.
- 프론트엔드 화면에서 사용할 수 있는 검색 조건과 페이지네이션 응답을 함께 고려해 API를 확장했습니다.

## 2026-06-14 알림 서비스

### 문제

계약 요청, 제안, 최종 계약, 배송기사 계약처럼 사용자가 놓치면 안 되는 상태 변경이 생겨도 이를 사용자별 이력으로 확인할 수 있는 구조가 없었습니다.

특히 화주는 새 제안 도착, 대리점은 제안 수락/거절, 배송기사는 계약 요청 수신 같은 이벤트를 화면에서 확인해야 합니다. 단순 실시간 알림보다 먼저 필요한 것은 나중에 다시 조회할 수 있는 알림 이력이었습니다.

### 해결

알림을 Redis 임시 데이터가 아니라 MySQL 기반 사용자 이력으로 모델링했습니다. Redis는 추후 미확인 카운트 캐시나 SSE/WebSocket pub-sub 확장 지점으로 남겼습니다.

핵심 도메인:

```kotlin
Notification
NotificationType
NotificationReferenceType
NotificationPublisher
NotificationService
```

초기 알림 타입:

```text
PROPOSAL_SUBMITTED
PROPOSAL_UPDATED
PROPOSAL_WITHDRAWN
PROPOSAL_ACCEPTED
PROPOSAL_REJECTED
CONTRACT_CREATED
DELIVER_CONTRACT_REQUESTED
DELIVER_CONTRACT_ACCEPTED
DELIVER_CONTRACT_REJECTED
```

알림 조회 API:

```text
GET /api/v1/notifications/me
GET /api/v1/notifications/me/unread-count
PUT /api/v1/notifications/{notificationId}/read
PUT /api/v1/notifications/me/read-all
```

### 변경 범위

- `notifications` 테이블 추가
- 알림 도메인 객체와 repository interface 추가
- JPA Entity/Repository 구현체 추가
- 알림 목록, 미확인 카운트, 읽음 처리 API 추가
- 제안 제출/수정/철회 시 화주에게 알림 생성
- 제안 수락/거절 및 계약 생성 시 대리점/화주에게 알림 생성
- 배송기사 계약 요청/수락/거절 시 대리점/배송기사에게 알림 생성
- `end_points` DML에 알림 API 권한 추가

### 검증

```bash
./gradlew compileKotlin
./gradlew test
```

### 포트폴리오 포인트

- 실시간 기능부터 붙이지 않고 먼저 영속 알림 이력을 도메인으로 모델링했습니다.
- 알림 생성 책임을 `NotificationPublisher`/`NotificationService`로 모아 계약 서비스 내부에 문구 생성 로직이 흩어지지 않도록 했습니다.
- 알림 대상, 발신자, 참조 도메인 타입과 ID를 함께 저장해 프론트엔드에서 상세 화면 이동이 가능하도록 설계했습니다.

## 2026-06-14 양방향 계약 요청 모델

### 문제

초기 계약 요청은 화주가 먼저 물량을 등록하고 대리점이 제안하는 단방향 구조였습니다.

하지만 실제 영업 흐름에서는 대리점이 먼저 특정 화주에게 조건을 제안할 수도 있습니다. 즉 중요한 것은 "누가 먼저 시작했는가"보다 계약 협상 흐름입니다.

```text
계약 요청/오퍼 생성
-> 조건 확인 또는 단가 조절
-> 승인/거절
-> 최종 계약
```

기존 `vendorId` 중심 `ContractRequest`는 대리점 -> 화주 오퍼를 표현하기 어려웠습니다.

### 해결

`ContractRequest`를 화주와 대리점 사이의 양방향 계약 요청/오퍼 루트로 일반화했습니다.

추가한 타입:

```kotlin
enum class ContractRequestType {
    VENDOR_OFFER, // 화주 -> 대리점
    AGENCY_OFFER, // 대리점 -> 화주
}

enum class ContractPartyType {
    VENDOR,
    AGENCY,
}
```

`ContractRequest`는 이제 요청자와 승인자를 명시합니다.

```text
requesterType
requesterId
approverType
approverId
```

`approverId = null`이면 공개 요청입니다. 예를 들어 화주가 여러 대리점에게 공개로 계약 요청을 열어두는 경우입니다.

추가 API:

```text
GET /api/v1/contract-requests/received
POST /api/v1/contract-requests/{contractRequestId}/accept
POST /api/v1/contract-requests/{contractRequestId}/reject
```

수락 시에는 기존 `Contract` 구조를 유지하기 위해 내부적으로 `ACCEPTED` 상태의 `Proposal`을 생성하고, 이를 기반으로 최종 계약을 생성합니다.

### 변경 범위

- `ContractRequestType`, `ContractPartyType` 추가
- `ContractRequest`에 requester/approver 모델 추가
- `contract_requests` DDL에서 `vendor_id` 중심 구조를 requester/approver 구조로 변경
- 요청자로 생성한 계약 요청 목록 조회
- 승인자로 받은 계약 요청 목록 조회
- 승인자의 계약 요청 수락/거절 API 추가
- 대리점 제안 가능 요청 조회 범위를 `VENDOR_OFFER + OPEN + approverId null 또는 내 agencyId`로 제한
- Querydsl 기반 비관락 조회를 requester/approver 기준으로 변경
- `end_points` DML에 신규 계약 요청 API 권한 추가

### 검증

```bash
./gradlew compileKotlin
./gradlew test
```

### 포트폴리오 포인트

- 단방향 입찰 구조에서 양방향 계약 협상 구조로 도메인을 확장했습니다.
- 별도 `AgencyOffer` 엔티티를 만들지 않고 `ContractRequest`의 타입과 당사자 모델을 확장해 중복 도메인 생성을 피했습니다.
- 대리점 공개 일감 탐색과 특정 대상 오퍼를 같은 루트에서 표현할 수 있도록 설계했습니다.
- 기존 `Proposal -> Contract` 생성 구조를 재사용해 변경 범위를 줄였습니다.

## 2026-06-25 DB 기반 API 권한 검사 성능 개선

### 문제

API 권한은 `@EndpointAccess` custom annotation으로 컨트롤러에 선언하고, 애플리케이션 시작 시 신규 API만 `end_points` 테이블에 기본 등록하는 구조로 만들었습니다.

이 구조는 신규 API 누락을 줄이고 DB에서 운영 권한 정책을 확인할 수 있다는 장점이 있었습니다. 하지만 초기 구현에서는 `EndPointAuthorizationFilter`가 모든 보호 API 요청마다 `EndPointRepository.findAll()`로 `end_points` 전체를 조회했습니다.

문제는 권한 정책의 변경 빈도는 낮지만 읽기 빈도는 매우 높다는 점이었습니다. 트래픽이 늘면 실제 비즈니스 로직보다 권한 필터에서 불필요한 DB I/O가 반복될 수 있었습니다.

### 측정

알림 미확인 수 조회 API를 대상으로 ApacheBench 성능 테스트를 진행했습니다.

```text
GET /api/v1/notifications/me/unread-count
```

개선 전 기준 측정:

| 조건 | RPS | 평균 지연 | p95 | p99 |
| --- | ---: | ---: | ---: | ---: |
| 1000 requests / concurrency 50 | 847.18 | 59ms | 126ms | 184ms |
| 5000 requests / concurrency 100 | 1,342.82 | 74ms | 136ms | 180ms |

### 해결

권한 정책의 source of truth는 DB로 두고, `@EndpointAccess`는 신규 API 누락 방지용 기본값으로만 사용하도록 정리했습니다. 애플리케이션 시작 시 기존 endpoint의 roles는 덮어쓰지 않고, DB에서 읽은 권한 정책을 메모리 캐시에 적재해 요청 시점에는 캐시에서 URL pattern, HTTP method, role을 검사하도록 변경했습니다.

운영 중 DB 권한 정책을 수정해야 하는 경우도 고려했습니다. 서버 재시작 없이 반영할 수 있도록 관리자용 운영 API를 추가했습니다.

```text
GET /api/v1/admin/end-points/cache/dry-run
POST /api/v1/admin/end-points/cache/reload
```

`dry-run`은 DB 정책과 현재 캐시의 차이를 보여주고, `reload`는 DB 기준으로 캐시를 갱신합니다. 이를 통해 요청 경로의 DB I/O는 제거하면서도 운영 중 권한 변경 가능성은 유지했습니다.

### 결과

개선 후 같은 조건으로 다시 측정했습니다.

| 조건 | 개선 전 RPS | 개선 후 RPS | 개선 전 평균 지연 | 개선 후 평균 지연 |
| --- | ---: | ---: | ---: | ---: |
| 1000 requests / concurrency 50 | 847.18 | 1,099.27 | 59ms | 45ms |
| 5000 requests / concurrency 100 | 1,342.82 | 2,583.01 | 74ms | 39ms |

`5000/c100` 조건에서는 RPS가 약 `92.4%` 증가했고, 평균 지연 시간은 약 `47.3%` 감소했습니다.

부하 테스트 중 Hibernate SQL 로그에서도 알림 count 쿼리는 반복되었지만, 요청마다 `end_points` 전체 조회가 반복되는 패턴은 관찰되지 않았습니다.

### 변경 범위

- `EndPointAuthorizationCache` 추가
- `EndPointAuthorizationFilter`가 repository 직접 조회 대신 캐시 사용
- `EndPointAutoRegistrar`가 endpoint 동기화 후 캐시 reload 수행
- 권한 캐시 dry-run/reload 관리자 API 추가
- 권한 캐시 단위 테스트 추가
- 요청마다 repository를 다시 조회하지 않는 필터 테스트 추가
- 성능 테스트 결과를 `.codex/performance.md`에 문서화

### 검증

```bash
./gradlew compileKotlin
./gradlew test
```

성능 테스트:

```bash
ab -n 1000 -c 50 -C "accessToken=..." http://127.0.0.1:8081/api/v1/notifications/me/unread-count
ab -n 5000 -c 100 -C "accessToken=..." http://127.0.0.1:8081/api/v1/notifications/me/unread-count
```

### 포트폴리오 포인트

- custom annotation으로 신규 API의 기본 권한 선언을 코드에 가깝게 두고, 실제 운영 권한 정책은 DB를 source of truth로 유지했습니다.
- DB 기반 권한 관리의 운영 장점과 요청마다 DB를 읽는 성능 비용 사이의 trade-off를 분석했습니다.
- 성능 테스트로 병목 가능성을 확인한 뒤, 메모리 캐시와 명시적 reload API로 개선했습니다.
- 단순히 캐시만 넣지 않고 `dry-run`을 추가해 운영자가 반영 전 변경 범위를 확인할 수 있게 했습니다.

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
- `AGENCY_OFFER` 프론트 플로우와 상세 QA
- 알림 SSE/WebSocket 실시간 전달 검토

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
