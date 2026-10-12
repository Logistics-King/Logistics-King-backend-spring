# 권한 캐시 개선

- Endpoint 권한의 source of truth는 DB다.
- 애플리케이션은 권한 조회 결과를 로컬 캐시에 보관한다.
- 신규 API는 Annotation으로 누락을 탐지하고 DB 권한은 운영에서 변경할 수 있다.
- dry-run은 누락·불일치만 확인하고, reload는 DB 정책을 다시 읽는다.
- 운영 권한 변경 후 서버 재시작 없이 캐시를 갱신할 수 있어야 한다.
