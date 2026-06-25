# 코드 컨벤션

## 기준

이 프로젝트의 Kotlin 코드는 JetBrains Kotlin 공식 코딩 컨벤션을 기본 기준으로 한다.

- Kotlin 공식 컨벤션을 Java Google Style보다 우선한다.
- formatter, ktlint, spotless 같은 자동 포맷 도구가 도입되면 자동 포맷 결과를 우선한다.
- 자동 포맷 도구와 충돌하는 수동 스타일은 만들지 않는다.

## DTO

HTTP request/response DTO는 API 계약을 설명하는 코드이므로 필드 가독성을 우선한다.

- `@field:Schema`는 설명 대상 필드 바로 위에 둔다.
- `@field:Schema`와 대상 `val` 사이에는 빈 줄을 두지 않는다.
- 다음 필드 묶음과는 빈 줄 하나를 둔다.

```kotlin
@field:Schema(description = "픽업 지역", example = "경기도 안산시 일동")
val pickupRegion: String,

@field:Schema(description = "픽업 상세 주소", example = "경기도 안산시 상록구 일동 101호")
val pickupAddress: String?,
```

## JPA Entity

JPA Entity는 DB 매핑 정보와 필드를 한 묶음으로 읽을 수 있게 작성한다.

- `@Id`, `@Column`, `@Enumerated` 같은 매핑 annotation은 대상 필드와 붙여 둔다.
- annotation 여러 개는 서로 붙여 둔다.
- 다음 필드 묶음과는 빈 줄 하나를 둔다.

```kotlin
@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false, length = 30)
val status: ContractStatus,

@Column(name = "memo", length = 255)
val memo: String?,
```

## Domain

Domain 코드는 Kotlin 공식 컨벤션을 기본으로 하되, 큰 aggregate는 읽기 편한 묶음을 허용한다.

- 단순 data class, value object, enum constructor는 Kotlin 공식 컨벤션처럼 붙여 쓴다.
- 필드가 많고 의미 설명이 필요한 aggregate는 주석과 필드를 한 묶음으로 두고, 다음 묶음과 빈 줄 하나를 둘 수 있다.
- 함수 내부 local variable에는 빈 줄을 강제하지 않는다.

```kotlin
data class TokenClaims(
    val userId: UUID,
    val role: UserRole,
)
```

```kotlin
class ContractRequest private constructor(
    // 계약 요청 식별자.
    val id: UUID,

    // 계약 요청 타입.
    val type: ContractRequestType,
)
```

## Naming

- 서비스명은 `택배왕`을 사용한다.
- 배송 품목 도메인은 `Product`를 사용한다.
- 테이블명은 `products`를 사용한다.
- 과거 명칭인 `VendorProduct`는 새 코드에 사용하지 않는다.

## Imports

- 사용하지 않는 import는 남기지 않는다.
- wildcard import는 사용하지 않는다.
- IDE 또는 Kotlin formatter의 기본 import 정렬을 따른다.

## Tests

- 테스트 이름은 동작을 설명하는 한글 backtick 이름을 허용한다.
- 테스트 헬퍼 이름도 도메인 용어를 따른다.
- 테스트에서도 새 도메인 명칭을 사용한다. 예: `Product`, `ProductRepository`
