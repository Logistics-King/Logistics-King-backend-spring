# Request DTO 규칙

- Controller는 Request DTO를 받고 command로 변환한다.
- Bean Validation은 Request DTO에 선언한다.
- 도메인 불변식은 Service/Domain에서 다시 검증한다.
- 등록과 수정의 입력 규칙이 다르면 Request 타입을 분리한다.
- 여러 배송 물품은 `items[]`로 받고, 박스 규격·수량·콜드체인 조건이 다르면 item을 별도 라인으로 받는다.
- enum은 API에서 enum name을 사용한다.
- 사용자나 소유자 ID는 요청값으로 받지 않고 인증 주체에서 결정한다.
