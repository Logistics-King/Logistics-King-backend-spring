# Controller 규칙

- Controller는 입력 변환, UseCase 호출, 응답 변환만 담당한다.
- 인증 주체는 `@AuthenticationPrincipal`로 받는다.
- 권한은 Controller 메서드에 `@EndpointAccess`로 명시한다.
- Swagger `@Operation`에는 실제 API 목적과 권한을 설명한다.
- 리소스는 복수형 명사를 사용한다.
- 현재 사용자 리소스는 `/me`를 사용한다.
- 상태 변경은 명시적인 동사 path를 사용한다. 예: `/cancel`, `/accept`, `/reject`.
- 성공·실패 응답 구조를 Controller마다 임의로 바꾸지 않는다.
