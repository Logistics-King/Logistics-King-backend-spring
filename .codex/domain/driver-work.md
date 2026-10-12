# 배송기사 일감

- `DeliverContract`: 근무 가능 지역, 단가, 계약 기간
- `DriverWork`: 최종 계약 물량에서 만들어진 지역·기간·물량 단위
- `DriverWorkApplication`: 기사의 일감 신청 기록

## 생성과 권한

- 대리점은 자기 최종 계약에 대해서만 일감을 생성한다.
- `OPEN` 등록은 소속 기사들이 신청하는 방식이다.
- 직접 할당은 특정 소속 기사를 지정해 바로 `ASSIGNED`로 만든다.
- 프리랜서는 DriverWork가 아니라 별도 계약 요청 흐름을 사용한다.
- 소속 기사는 자기 대리점의 OPEN 일감만 조회·신청한다.

## 상태

- 일감: `OPEN`, `ASSIGNED`, `CANCELLED`, `COMPLETED`
- 신청: `APPLIED`, `WITHDRAWN`, `SELECTED`, `REJECTED`

신청자를 선택하면 선택 신청은 `SELECTED`, 나머지는 `REJECTED`, 일감은 `ASSIGNED`가 된다.
