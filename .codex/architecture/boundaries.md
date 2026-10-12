# 도메인 경계와 테스트

- 도메인 규칙은 명시적인 Domain 객체 또는 Domain Service에 둔다.
- 여러 도메인의 흐름은 Application Service가 조합한다.
- 실패 가능한 상태 전이는 ErrorCode로 명시한다.
- 불필요한 generic abstraction을 추가하지 않는다.
- Domain, Application, Persistence 테스트를 책임에 맞게 분리한다.
