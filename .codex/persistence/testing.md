# Persistence 테스트

- Domain 테스트와 Persistence 테스트를 분리한다.
- Repository 조회는 조건, 정렬, 페이지네이션, soft delete를 검증한다.
- Projection과 Join 조회는 누락 필드와 중복 행을 검증한다.
- 스키마 변경 시 DDL/DML 초기화와 기존 데이터 호환성을 확인한다.
- 성능이 중요한 조회는 실제 실행계획과 인덱스 사용 여부를 확인한다.
