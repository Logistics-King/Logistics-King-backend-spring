# 페이지네이션과 날짜

## 페이지네이션

- Spring `Pageable`을 사용한다.
- 기본 page size는 API 목적에 맞게 제한한다.
- 응답에는 `items`, `page`, `size`, `totalElements`, `totalPages`, `hasNext`, `hasPrevious`를 포함한다.
- 정렬 기준은 서버에서 허용된 필드만 받는다.
- 집계·중복 제거가 필요한 API는 DB에서 처리한 뒤 페이지네이션한다.

## 날짜와 시간

- 날짜는 `yyyy-MM-dd` 형식이다.
- 시간은 `HH:mm` 형식이다.
- 반복 계약은 날짜 범위와 반복 조건을 구분한다.
- 서버 저장 시 사용하는 시간대와 API 표시 시간대를 명확히 한다.
