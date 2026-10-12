# 계층과 의존 방향

- `app`: Controller, DTO, UseCase, 애플리케이션 흐름
- `domain`: 도메인 객체, 정책, Repository interface
- `infra`: JPA, Querydsl, Redis, 외부 기술 구현

의존 방향은 `app -> domain <- infra`를 기본으로 한다. Domain이 Spring Data나 JPA 구현에 직접 의존하지 않게 한다.
