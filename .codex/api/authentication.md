# 인증·권한·회원가입 API

## 인증

- Access token과 Refresh token은 쿠키 기반으로 전달한다.
- 인증이 필요한 API는 인증 주체의 `userId`를 기준으로 소유권을 확인한다.
- URL이나 query로 전달받은 사용자 ID를 소유권 기준으로 신뢰하지 않는다.

## 권한

- Endpoint 권한은 DB를 source of truth로 관리한다.
- 신규 API는 Controller의 `@EndpointAccess`로 등록 의도를 명시한다.
- 운영 권한 변경은 DB 갱신과 권한 캐시 reload 정책을 따른다.
- 공개 API는 `publicAccess = true`로 명시한다.

## 회원가입

- 역할별 Request DTO를 사용한다.
- 역할에 필요한 프로필을 회원가입 과정에서 함께 생성할 수 있다.
- 화주는 Vendor, 대리점은 Agency, 배송기사는 Deliver 프로필을 생성한다.
