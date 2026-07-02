# API 작성 규칙

## 목표

택배왕 API는 프로토타입 단계에서도 일관된 URL, DTO, 응답 포맷을 유지한다.

Controller는 `app` 계층에 둔다. Controller는 HTTP DTO를 command로 변환하고 UseCase를 호출한 뒤, result를 response DTO로 변환한다.

## URL 규칙

모든 API는 `/api/v1` 이후에 작성한다.

```text
/api/v1/{resource}
/api/v1/{resource}/{id}
/api/v1/{resource}/{id}/{action}
```

예시:

```text
POST /api/v1/auth/sign-up/vendor
POST /api/v1/auth/sign-up/agency
POST /api/v1/auth/sign-up/driver
POST /api/v1/users
GET /api/v1/users/{userId}

POST /api/v1/contract-requests
GET /api/v1/contract-requests?page=0&size=20
GET /api/v1/contract-requests/{contractRequestId}
POST /api/v1/contract-requests/{contractRequestId}/proposals
POST /api/v1/proposals/{proposalId}/accept
GET /api/v1/proposals/{proposalId}/negotiations
POST /api/v1/proposals/{proposalId}/negotiations/price-offers
POST /api/v1/proposals/{proposalId}/negotiations/{eventId}/accept
POST /api/v1/proposals/{proposalId}/negotiations/{eventId}/reject
GET /api/v1/notifications/stream
GET /api/v1/recommendations/agencies
GET /api/v1/recommendations/vendors
GET /api/v1/agencies?agencyName=CJ%20일동&page=0&size=20
GET /api/v1/agencies/{agencyId}
GET /api/v1/delivers/agency/me?page=0&size=20
GET /api/v1/delivers/agency/me?active=true&serviceRegion=경기도%20안산시%20일동&driverName=김택배&vehicleNumber=12가&page=0&size=20
GET /api/v1/delivers/freelancers?active=true&serviceRegion=경기도%20안산시%20일동&driverName=김택배&vehicleNumber=12가&page=0&size=20
GET /api/v1/deliver-contracts/agency/me?status=REQUESTED&serviceRegion=경기도%20안산시%20일동&startDateFrom=2026-06-01&startDateTo=2026-12-31&page=0&size=20
GET /api/v1/deliver-contracts/driver/me?status=REQUESTED&serviceRegion=경기도%20안산시%20일동&startDateFrom=2026-06-01&startDateTo=2026-12-31&page=0&size=20
```

규칙:

- URL resource는 kebab-case를 사용한다.
- resource 이름은 가능하면 복수형을 사용한다.
- 행위가 필요한 경우 마지막 segment에 action을 둔다.
- API version은 URL prefix로 관리한다.

## 대리점 검색/상세 조회 사용처

`GET /api/v1/agencies`와 `GET /api/v1/agencies/{agencyId}`는 화주의 계약 요청 대상 대리점 선택과 배송기사의 소속 대리점 선택에서 함께 사용한다.

배송기사 프로필 등록 화면에서 `employmentType=AGENCY_AFFILIATED`를 선택한 경우 다음 흐름을 사용한다.

1. 배송기사가 대리점명을 입력한다.
2. 프론트는 `GET /api/v1/agencies?agencyName={검색어}&page=0&size=20&scope=ALL`로 후보 목록을 조회한다.
3. 목록에서 `agencyId`, `carrier`, `agencyName`, `mainRegion`, `serviceRegions`를 보여준다.
4. 상세 정보가 필요하면 `GET /api/v1/agencies/{agencyId}`를 호출한다.
5. 상세 화면에서는 `representativeName`, `phoneNumber`, `address`, `addressDetail`, `serviceRegions`, `supportedColdChainTypes`, `maxMonthlyVolume`까지 보여줄 수 있다.
6. 배송기사 회원가입 또는 프로필 생성/수정 요청에는 선택한 `agencyId`를 넣는다.

권한:

- `GET /api/v1/agencies`: `PUBLIC`
- `GET /api/v1/agencies/{agencyId}`: `PUBLIC`

`PUBLIC`은 실제 사용자 역할이 아니라 endpoint 접근 정책이다. 회원가입 전 배송기사도 소속 대리점을 검색해야 하므로, 해당 API는 인증 없이 호출할 수 있다.

배송기사에게 `scope=NEARBY`는 별도 기준 지역이 없으므로 현재는 `ALL`처럼 처리한다. 화주에게만 `NEARBY`가 화주 주요 지역 기준으로 적용된다.

## 회원가입과 프로필 생성 규칙

역할별 회원가입 API는 계정과 해당 역할의 프로필을 한 번에 생성한다.

- `POST /api/v1/auth/sign-up/vendor`: 화주 계정과 화주 프로필 생성
- `POST /api/v1/auth/sign-up/agency`: 대리점 계정과 대리점 프로필 생성
- `POST /api/v1/auth/sign-up/driver`: 배송기사 계정과 배송기사 프로필 생성

트랜잭션 기준:

- 계정 생성과 프로필 생성은 같은 트랜잭션에서 처리한다.
- 프로필 생성이 실패하면 계정 생성도 rollback된다.
- 로그인 ID나 이메일 중복이면 프로필 생성 전에 실패한다.

배송기사 회원가입 request에는 `employmentType`을 함께 보낸다.

- `AGENCY_AFFILIATED`: 특정 대리점 소속 기사. `agencyId` 필수.
- `FREELANCER`: 프리랜서 기사. `agencyId`는 null 가능.

배송기사 회원가입 화면에서 대리점 소속을 선택한 경우 먼저 `GET /api/v1/agencies?agencyName={검색어}&scope=ALL`로 소속 대리점을 검색하고, 선택한 `agencyId`를 `POST /api/v1/auth/sign-up/driver` request에 넣는다. 프리랜서를 선택한 경우 대리점 검색 없이 `agencyId=null`로 가입할 수 있다.

별도 프로필 생성 API는 수정/재등록 흐름 또는 관리자/운영 보정용으로 유지한다.

## SSE 알림 규칙

알림 SSE는 `GET /api/v1/notifications/stream`을 사용한다.

요청:

```text
GET /api/v1/notifications/stream
Cookie: accessToken=...
Last-Event-ID: {notificationId}   # 선택, 브라우저 EventSource 재연결 시 자동 전송
```

이벤트:

```text
event: connected
data: {"connected":true}

id: {notificationId}
event: notification
data: NotificationDetailResponse
```

규칙:

- 서버는 알림을 DB에 저장한 뒤 트랜잭션 커밋 이후 SSE로 전달한다.
- `Last-Event-ID`가 유효하면 해당 알림 이후 저장된 알림을 최대 100개까지 오래된 순서로 재전송한다.
- `Last-Event-ID`가 없거나 본인 알림이 아니면 replay 없이 연결만 유지한다.
- SSE는 최소 1회 전달 정책이므로 중복 수신 가능성이 있다.
- 프론트는 `notificationId` 기준으로 중복 제거한다.
- 최종 상태 복구는 `GET /api/v1/notifications/me`를 사용한다.

## Request DTO 규칙

Request DTO는 도메인별 sealed interface로 묶는다.

형식:

```text
{Domain}Request.{Action}
```

예시:

```kotlin
sealed interface UserRequest {
    data class Create(
        val name: String,
        val phoneNumber: String,
    ) : UserRequest
}
```

계약 요청 예시:

```kotlin
sealed interface ContractRequestRequest {
    data class Create(
        val shipperId: Long,
        val pickupRegion: String,
        val monthlyVolume: Int,
        val productType: String,
        val boxSize: String,
        val pickupStartTime: String,
        val pickupEndTime: String,
        val requiredTerms: List<String>,
    ) : ContractRequestRequest
}
```

규칙:

- Request DTO는 `app.{domain}.dto` 또는 `app.{domain}` 하위에 둔다.
- Request DTO는 HTTP 요청 모양만 표현한다.
- Request DTO를 domain service까지 전달하지 않는다.
- Controller에서 Request DTO를 command로 변환한다.

```kotlin
fun ContractRequestRequest.Create.toCommand(): CreateContractRequestCommand {
    return CreateContractRequestCommand(
        shipperId = shipperId,
        pickupRegion = pickupRegion,
        monthlyVolume = monthlyVolume,
        productType = productType,
        boxSize = boxSize,
        pickupStartTime = pickupStartTime,
        pickupEndTime = pickupEndTime,
        requiredTerms = requiredTerms,
    )
}
```

## Response DTO 규칙

Response DTO도 도메인별 sealed interface로 묶는다.

형식:

```text
{Domain}Response.{Action}
```

예시:

```kotlin
sealed interface UserResponse {
    data class Create(
        val userId: Long,
    ) : UserResponse
}
```

계약 요청 예시:

```kotlin
sealed interface ContractRequestResponse {
    data class Create(
        val contractRequestId: Long,
    ) : ContractRequestResponse

    data class Detail(
        val contractRequestId: Long,
        val pickupRegion: String,
        val monthlyVolume: Int,
        val proposalCount: Int,
    ) : ContractRequestResponse
}
```

규칙:

- Response DTO는 HTTP 응답 모양만 표현한다.
- UseCase result를 그대로 노출하지 않는다.
- Controller 또는 response mapper 함수에서 result를 Response DTO로 변환한다.

## 공통 응답 포맷

모든 응답은 `payload`로 감싼다.

`payload`에는 다음 필드를 둔다.

- `code`: 응답 코드
- `errorMessage`: 에러 메시지. 성공이면 `null`
- `response`: 실제 응답 객체. 실패이면 `null`

Kotlin 예시:

```kotlin
data class ApiResponse<T>(
    val payload: ApiPayload<T>,
) {
    companion object {
        fun <T> success(
            code: String,
            response: T,
        ): ApiResponse<T> {
            return ApiResponse(
                payload = ApiPayload(
                    code = code,
                    errorMessage = null,
                    response = response,
                )
            )
        }

        fun error(
            code: String,
            errorMessage: String,
        ): ApiResponse<Nothing> {
            return ApiResponse(
                payload = ApiPayload(
                    code = code,
                    errorMessage = errorMessage,
                    response = null,
                )
            )
        }
    }
}

data class ApiPayload<T>(
    val code: String,
    val errorMessage: String?,
    val response: T?,
)
```

성공 응답 예시:

```json
{
  "payload": {
    "code": "SUCCESS",
    "errorMessage": null,
    "response": {
      "contractRequestId": 1
    }
  }
}
```

실패 응답 예시:

```json
{
  "payload": {
    "code": "CONTRACT_REQUEST_NOT_FOUND",
    "errorMessage": "계약 요청을 찾을 수 없습니다.",
    "response": null
  }
}
```

## Controller 작성 규칙

Controller는 얇게 유지한다.

Controller 역할:

- URL mapping
- HTTP request DTO validation
- Request DTO -> command 변환
- UseCase 호출
- result -> Response DTO 변환
- `ApiResponse`로 감싸기

Controller가 하지 않는 것:

- 비즈니스 규칙 판단
- repository 직접 호출
- JPA Entity 참조
- Querydsl 사용

예시:

```kotlin
@RestController
@RequestMapping("/api/v1/contract-requests")
class ContractRequestController(
    private val createContractRequestUseCase: CreateContractRequestUseCase,
) {

    @PostMapping
    fun create(
        @RequestBody request: ContractRequestRequest.Create,
    ): ApiResponse<ContractRequestResponse.Create> {
        val result = createContractRequestUseCase.create(request.toCommand())

        return ApiResponse.success(
            code = "SUCCESS",
            response = ContractRequestResponse.Create(
                contractRequestId = result.contractRequestId,
            )
        )
    }
}
```

## Naming 규칙

- sealed interface 이름은 `{Domain}Request`, `{Domain}Response`를 사용한다.
- 하위 data class 이름은 행위 기준으로 작성한다.
- 생성: `Create`
- 수정: `Update`
- 상세 조회: `Detail`
- 목록 조회: `List`
- 검색: `Search`
- 선택: `Select`
- 제출: `Submit`

예시:

```text
UserRequest.Create
UserResponse.Create
ContractRequestRequest.Create
ContractRequestResponse.Detail
ProposalRequest.Submit
ProposalResponse.Select
```

## Pagination 응답

목록 응답도 `payload.response` 안에 넣는다.

```kotlin
data class PageResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
)
```

예시:

```json
{
  "payload": {
    "code": "SUCCESS",
    "errorMessage": null,
    "response": {
      "items": [],
      "page": 0,
      "size": 20,
      "totalElements": 0,
      "totalPages": 0,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

목록 API 기본 쿼리:

```text
?page=0&size=20
```

## Date and time

- API 날짜/시간 문자열은 ISO-8601 형식을 사용한다.
- 서버 내부 시간 타입은 `LocalDate`, `LocalDateTime`을 우선 사용한다.
- timezone이 필요한 외부 연동에서는 `OffsetDateTime` 사용을 검토한다.

예시:

```text
2026-05-03
2026-05-03T23:30:00
2026-05-03T23:30:00+09:00
```
