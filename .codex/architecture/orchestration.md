# Backend Orchestration

## 작업 순서

1. 요구사항과 기존 동작을 분석한다.
2. 필요한 Domain 문서와 API·Persistence 문서만 읽는다.
3. 변경 파일, 계약, 트랜잭션, 권한, 테스트 계획을 정리한다.
4. 승인된 계약 기준으로 구현한다.
5. 컴파일·테스트·API 검증을 실행한다.
6. 문서와 프론트 handoff를 갱신한다.
7. 변경 의도별로 커밋을 분리한다.

## 게이트

- Planning: 영향 범위와 구현 전략 확인
- Contract: URL, DTO, Repository, 권한, 오류 코드 확정
- Implementation: 작은 단위로 구현
- Integration: 계층·패키지·의존성 확인
- Verification: `compileKotlin`, `test`, API·DB 확인
- Closeout: 변경·검증·위험·handoff 보고

## 실행 원칙

- 작은 작업은 serial로 진행한다.
- 병렬 작업은 계약이 확정되고 파일 범위가 겹치지 않을 때만 사용한다.
- 요청하지 않은 구조 변경과 불필요한 추상화를 추가하지 않는다.
- 모든 문서를 한 번에 읽지 않는다.
