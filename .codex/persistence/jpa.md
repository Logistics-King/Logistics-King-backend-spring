# JPA 기본 규칙

- JPA Entity는 `infra.persistence`에 둔다.
- Domain 객체와 JPA Entity를 분리한다.
- Entity는 DB 매핑과 변환 책임만 가진다.
- Enum은 `EnumType.STRING`을 사용한다.
- UUID는 프로젝트의 시간순 ID 생성 규칙을 따른다.
- Soft delete가 필요한 공통 엔티티는 프로젝트 공통 Entity를 사용한다.
- 연관관계는 필요한 경우에만 명시하고, 무분별한 양방향 매핑을 피한다.
