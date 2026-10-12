# Fetch Join과 주의사항

- Fetch Join은 N+1 해결 목적일 때만 사용한다.
- 컬렉션 Fetch Join과 페이지네이션을 함께 사용하지 않는다.
- 목록 조회는 batch 조회 또는 Projection을 우선 검토한다.
- 조회 범위와 권한 조건을 Fetch Join 때문에 누락하지 않는다.
