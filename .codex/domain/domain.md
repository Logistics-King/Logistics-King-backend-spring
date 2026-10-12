# 택배왕 도메인 인덱스

택배왕은 화주와 지역 대리점을 연결해 택배 계약을 중개하는 플랫폼이다.

## 전체 흐름

```text
배송 물품 등록 -> 계약 요청 -> 대리점 제안 -> 단가 협상
-> 화주 제안 선택 -> 최종 계약 -> 기사 일감 배정
```

## 문서 선택

- 역할과 관계: `actors.md`
- 배송 물품: `product.md`
- 계약 요청과 일정: `contract-request.md`
- 제안과 단가 협상: `proposal.md`
- 최종 계약: `contract.md`
- 추천: `recommendation.md`
- 배송기사: `delivery-driver.md`, `driver-work.md`
- 알림과 SSE: `notification.md`
- 운영 로그: `operation-log.md`
- 미확정 정책: `open-decisions.md`

## 공통 원칙

- 계약은 화주와 대리점 사이에 체결된다.
- 대리점 제안은 해당 계약 요청의 화주에게만 공개한다.
- 가격뿐 아니라 서비스 조건과 평판을 함께 비교한다.
- 실제 집하·배송 운영은 대리점과 택배사 구조에 남긴다.
