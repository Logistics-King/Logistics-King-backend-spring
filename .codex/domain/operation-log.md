# 운영 로그

AccessLog는 비즈니스 원천 데이터가 아니라 API 운영 추적용 기록이다.

## 기록

`requestId`, method, path, queryString, statusCode, latencyMs, userId, userRole, errorCode, clientIp, userAgent, occurredAt을 저장한다.

## 금지

access/refresh token, cookie, password, request/response body 전체, 전화번호·주소·사업자등록번호 원문은 저장하지 않는다.

## 처리

- 로그 저장 실패가 원래 API 처리에 영향을 주면 안 된다.
- 요청 완료 후 이벤트를 발행하고 비동기 listener가 별도 트랜잭션으로 저장한다.
- 초기에는 Spring `@Async`, 트래픽 증가 후 Kafka·RabbitMQ·Redis Stream을 검토한다.
