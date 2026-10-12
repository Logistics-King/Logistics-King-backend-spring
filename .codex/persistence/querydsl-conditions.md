# Querydsl 조건

- 검색 조건은 BooleanExpression으로 분리한다.
- null 또는 공백 조건은 조건식에서 제외한다.
- 공통 조건은 재사용하되 도메인 정책을 generic helper에 숨기지 않는다.
- soft delete 조건은 조회 정책에 맞게 항상 적용한다.
- 사용자 소유권 조건은 인증된 도메인 ID를 기준으로 넣는다.
