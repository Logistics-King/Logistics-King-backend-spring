# 공통 API 규칙

## URL

- Base path는 `/api/v1`이다.
- 리소스는 복수형 명사를 사용한다.
- 현재 사용자 리소스는 `/me`를 사용한다.
- 상태 변경은 명시적인 동사 path를 사용한다. 예: `/cancel`, `/accept`, `/reject`.
- ID는 path variable로 전달하며, 소유자 ID를 query로 받지 않는다.

## Request DTO

- Controller는 Request DTO를 받고 command 변환은 DTO에 둔다.
- Bean Validation은 DTO에 선언한다.
- 도메인 규칙은 Service/Domain에서 다시 검증한다.
- 등록과 수정 Request는 필요하면 별도 타입으로 분리한다.

## Response DTO

- Domain 객체를 직접 반환하지 않는다.
- 외부 응답은 문자열 UUID와 enum name을 사용한다.
- 목록은 공통 페이지 응답을 사용한다.
- 민감 정보와 내부 구현 필드는 응답에서 제외한다.

## Controller

- 인증 주체는 `@AuthenticationPrincipal`로 받는다.
- 권한은 Controller 메서드에 `@EndpointAccess`로 명시한다.
- Swagger `@Operation`에는 실제 API 목적과 권한을 설명한다.
- Controller는 입력 변환, UseCase 호출, 응답 변환만 담당한다.

## 공통 응답

모든 API는 프로젝트의 `ApiResponse` 형식을 사용한다. 성공 응답과 오류 응답의 필드 구조를 임의로 바꾸지 않는다.
