# Querydsl Projection과 Join

- 응답에 필요한 필드만 Projection한다.
- 여러 도메인을 조인할 때 join 조건과 개인정보 범위를 확인한다.
- 1:N 조인으로 목록이 중복되지 않는지 검증한다.
- 집계 결과는 별도 read model로 반환할 수 있다.
- Fetch Join은 목록 페이징과 함께 사용할 때 결과 중복과 count 쿼리를 확인한다.
