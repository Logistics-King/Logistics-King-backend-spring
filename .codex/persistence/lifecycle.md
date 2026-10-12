# ID·Audit·Migration

- ID는 프로젝트의 `IdGenerator`를 통해 생성한다.
- 시간순 UUID는 정렬과 분산 생성 요구를 고려해 사용한다.
- 공통 Audit 필드는 생성 시각과 수정 시각을 기록한다.
- 생성·수정 시각은 애플리케이션 또는 JPA lifecycle 규칙을 일관되게 사용한다.
- 스키마 변경은 기존 테이블을 기준으로 하며 destructive migration은 피한다.
- 운영 DB 변경 전 기존 데이터, 인덱스, 롤백 방법을 확인한다.
