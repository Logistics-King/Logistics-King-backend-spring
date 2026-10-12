# 구현 패턴

- UseCase interface는 `app.usecase`에 둔다.
- Service는 UseCase를 구현하고 트랜잭션 경계를 가진다.
- Repository interface는 Domain, 구현체는 Infra에 둔다.
- Controller는 입력 변환과 UseCase 호출만 담당한다.
- DTO와 Domain 변환 책임을 명확히 한다.
- 패키지는 기능 도메인 기준으로 구성한다.
