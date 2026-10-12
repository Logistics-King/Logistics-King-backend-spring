# Querydsl Paging과 Aggregate

- DB에서 필터·중복 제거·정렬·집계를 수행한 뒤 페이지네이션한다.
- `offset`, `limit`, 정렬 필드는 서버에서 검증한다.
- `countDistinct`가 필요한지 확인한다.
- Group By 결과의 count 쿼리가 실제 목록 수와 일치하는지 테스트한다.
- 집계 정렬은 최신일, ID 등 안정적인 tie-breaker를 둔다.
