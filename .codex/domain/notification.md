# 알림과 SSE

알림 원천 저장소는 MySQL이다. Redis는 캐시·pub/sub 보조 수단으로만 사용한다.

## 저장

수신자·발신자, 타입, 제목·본문, 이동 URL, 참조 타입·ID, 읽은 시각, 생성·수정 시각을 저장한다. 초기 조회 범위는 최근 30일이며 삭제는 별도 scheduler 정책으로 둔다.

주요 타입:

- `CONTRACT_REQUEST_CREATED`
- `PROPOSAL_SUBMITTED`, `PROPOSAL_UPDATED`, `PROPOSAL_WITHDRAWN`
- `PROPOSAL_ACCEPTED`, `PROPOSAL_REJECTED`
- `CONTRACT_CREATED`, `CONTRACT_CANCELED`
- `DELIVER_CONTRACT_REQUESTED`, `DELIVER_CONTRACT_ACCEPTED`, `DELIVER_CONTRACT_REJECTED`

## 전달

- DB 저장 후 커밋 이후 SSE로 전달한다.
- 사용자별 emitter registry로 해당 수신자에게만 보낸다.
- SSE event id는 Notification ID다.
- `Last-Event-ID` 이후 DB 알림을 replay한다.
- 최소 1회 전달을 기준으로 하며 프론트는 notificationId로 중복 제거한다.
- 최종 복구 기준은 알림 목록 API다.
