# API 문서 인덱스

API 문서는 도메인별 API 목록이 아니라 모든 Controller에 공통 적용되는 작성 규칙을 관리한다.

- 공통 URL·DTO 기본 원칙: `common.md`
- Request DTO와 Validation: `request.md`
- Response DTO와 공통 응답: `response.md`
- Controller와 Swagger: `controller.md`
- 인증·권한·회원가입: `authentication.md`
- 페이지네이션·날짜·시간: `pagination.md`
- 공개 API와 개인정보: `public-api.md`

계약·대리점·배송기사·알림의 구체적인 비즈니스 정책은 `.codex/domain/` 문서를 기준으로 한다. 실제 endpoint 계약은 Controller와 OpenAPI 문서에서 확인하며, 프론트 전달이 필요한 변경은 API handoff 문서로 작성한다.
