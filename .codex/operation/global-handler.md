# GlobalExceptionHandler

- `GlobalException`은 정의된 ErrorCode 응답으로 변환한다.
- Validation 예외는 필드별 오류를 일관된 응답으로 반환한다.
- 처리하지 못한 예외는 내부 로그에 stack trace를 남기고 외부에는 일반 메시지만 반환한다.
- 예외 응답은 공통 `ApiResponse` 형식을 따른다.
- requestId를 응답과 로그에 연결한다.
