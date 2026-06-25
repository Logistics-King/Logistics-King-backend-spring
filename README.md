# 택배왕 Backend

택배왕은 화주와 택배 대리점을 연결해 택배 계약 요청, 제안, 비교, 계약 선택 흐름을 디지털화하는 물류 계약 플랫폼입니다.

현재 단계는 핵심 백엔드 기반을 구축하는 프로토타입입니다. 실제 서비스 확장을 고려해 인증, 권한, 도메인 계층, API 규칙을 먼저 정리하고 있습니다.

## 문제 정의

소상공인이나 쇼핑몰 운영자는 택배 계약을 맺기 위해 여러 택배 대리점에 직접 연락해야 합니다. 같은 택배사라도 지역 대리점마다 단가, 픽업 시간, 반품 조건, 특수 배송 가능 여부가 다를 수 있습니다.

택배왕은 화주가 한 번 계약 요청을 등록하면 여러 대리점이 비공개로 제안하고, 화주는 가격과 서비스 조건을 비교해 적합한 대리점을 선택하는 구조를 목표로 합니다.

## 주요 권한

- `ADMIN`: 플랫폼 관리자
- `VENDOR`: 화주, 쇼핑몰, 판매자
- `AGENCY`: 택배 대리점
- `DRIVER`: 배송기사

## 현재 구현 범위

- User domain과 JPA entity 분리
- UUIDv7 기반 사용자 ID 생성
- 권한별 회원가입 API
- JWT access token / refresh token 발급
- HttpOnly cookie 기반 인증
- Redis refresh token 저장
- `end_points` 테이블 기반 URL 권한 관리
- 신규 API 기본 `ADMIN` 권한 자동 등록
- GlobalException 기반 공통 예외 응답
- Swagger/OpenAPI 문서 설정
- 인증/권한 관련 단위 테스트

## 기술 스택

- Kotlin 2.2.21
- Java 21
- Spring Boot 4.0.6
- Spring Security
- Spring Data JPA
- MySQL
- Redis
- Gradle
- Springdoc OpenAPI

## 아키텍처

프로토타입 단계에서 과한 복잡도를 피하면서도 요구사항 변경에 대응하기 위해 `app`, `domain`, `infra` 3계층으로 구성합니다.

```text
app
- Controller
- Request/Response DTO
- UseCase interface
- Command/Result DTO

domain
- Domain object
- Domain service
- Repository interface
- Business rule
- ErrorCode

infra
- JPA entity
- Repository implementation
- Redis adapter
- Security
- Swagger
```

의존 방향은 `app -> domain`, `infra -> domain`, `infra -> app`을 기본으로 합니다. Domain 객체는 JPA entity나 HTTP DTO를 직접 알지 않도록 분리합니다.

## API 응답 형식

모든 API 응답은 `payload`로 감쌉니다.

```json
{
  "payload": {
    "code": "SUCCESS",
    "errorMessage": null,
    "response": {}
  }
}
```

## 주요 API

```text
POST /api/v1/auth/sign-up/vendor
POST /api/v1/auth/sign-up/agency
POST /api/v1/auth/sign-up/driver
POST /api/v1/auth/sign-in
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
```

기존 로그인 호환 경로도 유지합니다.

```text
POST /api/v1/auth/login
```

## 로컬 실행

MySQL database를 먼저 생성합니다.

```sql
CREATE DATABASE IF NOT EXISTS Logistics_King
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

IntelliJ Run Configuration의 `Environment variables` 예시입니다.

```text
DB_HOST=localhost;DB_PORT=3306;DB_NAME=Logistics_King;DB_USERNAME=root;DB_PASSWORD=1234;REDIS_HOST=localhost;REDIS_PORT=6379;JWT_SECRET=local-development-secret-key-for-logistics-king;JWT_ACCESS_TOKEN_EXPIRATION_SECONDS=900;JWT_REFRESH_TOKEN_EXPIRATION_SECONDS=1209600
```

실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

## 검증

```bash
./gradlew compileKotlin
./gradlew test
```

## 개발 규칙

프로젝트 규칙은 `.codex` 문서에 관리합니다.

- `.codex/domain.md`: 도메인 정의와 비즈니스 규칙
- `.codex/architecture.md`: 계층 구조와 의존 방향
- `.codex/api.md`: URL, Request/Response, 공통 응답 규칙
- `.codex/error-handling.md`: ErrorCode와 GlobalException 규칙
- `.codex/persistence.md`: JPA, SQL, DB 규칙
- `.codex/querydsl.md`: Querydsl 작성 패턴
- `.codex/performance.md`: 성능 테스트 기록과 개선 근거
- `.codex/commit.md`: 커밋 메시지와 diff 분리 규칙

## AI 개발 하네스

이 프로젝트는 AI를 단순 코드 생성 도구가 아니라 개발 프로세스를 보조하는 agent harness로 사용합니다.

주요 목적은 다음과 같습니다.

- 요구사항 분석
- 구현 전략 정리
- API, DTO, UseCase, transaction boundary 확인
- `app`, `domain`, `infra`, `test` 관심사 분리
- 테스트와 리뷰 피드백 루프
- 요구사항 단위 커밋 분리
- 백엔드/프론트엔드 스레드 간 API spec 전달

사용 중인 주요 skill:

- `commit-splitter`: 요구사항 단위 커밋 분리
- `postman-local-test`: 로컬 API 테스트와 DB 검증
- `api-spec-handoff`: 프론트엔드 스레드 전달용 API spec 정리

## Roadmap

- 화주 프로필
- 대리점 프로필
- 배송기사 프로필
- 계약 요청 등록/조회
- 대리점 제안 제출
- 제안 비교/선택
- 계약 생성
- 프론트엔드 연동
- 운영 profile 분리
- 배포 전 권한 DML 관리
