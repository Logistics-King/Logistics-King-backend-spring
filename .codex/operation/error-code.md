# ErrorCode 규칙

- Domain 예외는 도메인별 ErrorCode를 사용한다.
- ErrorCode에는 클라이언트가 이해할 수 있는 메시지와 HTTP 상태를 연결한다.
- Service는 상황에 맞는 구체적인 ErrorCode를 선택한다.
- 일반 예외를 그대로 외부에 노출하지 않는다.
- 도메인 불변식은 `requireDomain`으로 검증할 수 있다.
