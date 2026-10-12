# Entity와 Repository

## Entity

- Entity는 DB 컬럼과 Domain 변환만 담당한다.
- 도메인 정책은 Domain 객체나 Domain Service에 둔다.
- Entity의 nullable, unique, length, index 설정은 SQL과 일치시킨다.

## Repository

- Repository interface는 `domain`에 둔다.
- JPA/Querydsl 구현체는 `infra.persistence`에 둔다.
- 애플리케이션 계층은 Domain Repository interface만 의존한다.
- 조회 목적이면 필요한 필드만 Projection한다.
- N+1이 발생하지 않는지 목록 조회에서 확인한다.
