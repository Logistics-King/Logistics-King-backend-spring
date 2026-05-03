# 택배왕 아키텍처 가이드

## 목표

택배왕은 프로토타입이지만 요구사항 변경 가능성이 크다. 그래서 구조는 단순하게 유지하되, 핵심 경계는 지킨다.

기본 패키지는 `app`, `domain`, `infra` 3개로 나눈다.

```text
app
domain
infra
```

## 계층 역할

### app

`app`은 사용자 요청이 들어오는 입구와 유스케이스 계약을 담당한다.

포함한다:

- REST Controller
- HTTP request/response DTO
- UseCase interface
- application command/result DTO

하지 않는다:

- 핵심 비즈니스 규칙 구현
- JPA Entity 직접 사용
- 외부 API, DB, 파일 시스템 직접 접근

예시:

```text
app
  contract
    ContractRequestController
    CreateContractRequestUseCase
    SubmitProposalUseCase
    SelectProposalUseCase
    dto
    command
    result
```

### domain

`domain`은 택배왕의 핵심 비즈니스 규칙과 usecase 구현체를 담당한다.

포함한다:

- Entity
- Value Object
- Domain Service
- Policy
- Repository interface
- UseCase 구현 Service

하지 않는다:

- HTTP request/response DTO 참조
- Spring MVC 세부사항 참조
- JPA Entity 세부사항 참조
- 외부 API 구현 참조

예시:

```text
domain
  contract
    ContractRequest
    Proposal
    Contract
    ContractRequestRepository
    ProposalRepository
    ContractRequestService
    ProposalPolicy
```

### infra

`infra`는 외부 기술 구현을 담당한다.

포함한다:

- JPA Entity
- Spring Data Repository
- domain Repository 구현체
- 외부 API client
- 파일, 메시지, 메일, 결제 등 외부 기술 adapter

하지 않는다:

- 비즈니스 의사결정
- 유스케이스 흐름 결정
- HTTP Controller 역할

예시:

```text
infra
  persistence
    ContractRequestJpaEntity
    ContractRequestJpaRepository
    ContractRequestRepositoryImpl
  external
```

## 의존 방향

허용:

```text
app -> domain
infra -> domain
infra -> app
```

설명:

- `app.controller`는 `app.usecase` interface를 호출한다.
- `domain.service`는 `app.usecase` interface를 구현한다.
- `domain.service`는 `domain.repository` interface에 의존한다.
- `infra.persistence`는 `domain.repository` interface를 구현한다.
- Spring DI 구성을 위해 `infra`가 `app`과 `domain`을 조립할 수 있다.

금지:

```text
domain entity -> app dto
domain entity -> infra jpa entity
domain service -> controller
domain service -> http request/response dto
```

## 기본 구현 패턴

### UseCase interface

UseCase interface는 `app`에 둔다. Controller는 이 interface만 호출한다.

```kotlin
interface CreateContractRequestUseCase {
    fun create(command: CreateContractRequestCommand): CreateContractRequestResult
}
```

### UseCase 구현 Service

UseCase 구현체는 `domain`에 둔다. 실제 비즈니스 흐름과 도메인 객체 생성을 담당한다.

```kotlin
@Service
class ContractRequestService(
    private val contractRequestRepository: ContractRequestRepository,
) : CreateContractRequestUseCase {

    override fun create(command: CreateContractRequestCommand): CreateContractRequestResult {
        val request = ContractRequest.create(
            shipperId = command.shipperId,
            pickupRegion = command.pickupRegion,
            monthlyVolume = command.monthlyVolume,
            productType = command.productType,
            boxSize = command.boxSize,
            pickupTimeRange = command.pickupTimeRange,
        )

        val saved = contractRequestRepository.save(request)
        return CreateContractRequestResult(saved.id)
    }
}
```

### Repository interface

Repository interface는 `domain`에 둔다.

```kotlin
interface ContractRequestRepository {
    fun save(request: ContractRequest): ContractRequest
    fun findById(id: Long): ContractRequest?
}
```

### Repository implementation

Repository 구현체는 `infra`에 둔다.

```kotlin
@Repository
class ContractRequestRepositoryImpl(
    private val jpaRepository: ContractRequestJpaRepository,
) : ContractRequestRepository {

    override fun save(request: ContractRequest): ContractRequest {
        return jpaRepository.save(ContractRequestJpaEntity.from(request)).toDomain()
    }

    override fun findById(id: Long): ContractRequest? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }
}
```

## 패키지 예시

```text
src/main/kotlin/...
  app
    contract
      ContractRequestController.kt
      CreateContractRequestUseCase.kt
      SubmitProposalUseCase.kt
      SelectProposalUseCase.kt
      command
      result
      dto

  domain
    contract
      ContractRequest.kt
      Proposal.kt
      Contract.kt
      ContractRequestService.kt
      ProposalService.kt
      ContractRequestRepository.kt
      ProposalRepository.kt
      policy

  infra
    persistence
      contract
        ContractRequestJpaEntity.kt
        ContractRequestJpaRepository.kt
        ContractRequestRepositoryImpl.kt
        ProposalJpaEntity.kt
        ProposalJpaRepository.kt
        ProposalRepositoryImpl.kt
    external
```

## 도메인 구현 기준

구현 전 반드시 `.codex/domain.md`를 확인한다.

특히 다음 섹션을 기준으로 한다:

- `주요 도메인 객체 후보`
- `기본 매칭 기준`
- `입찰과 비교 규칙`

택배왕 핵심 규칙은 domain에 둔다:

- 대리점 제안은 비공개 입찰이다.
- 화주는 여러 제안을 한 화면에서 비교한다.
- 최저가만 선택 기준이 아니다.
- 동일 택배사라도 대리점별 가격과 서비스 조건이 다를 수 있다.
- 계약 요청 정보가 구체적일수록 대리점은 더 정확한 단가를 제안할 수 있다.

## DTO 규칙

- HTTP request/response DTO는 `app`에 둔다.
- command/result DTO도 `app`에 둔다.
- domain 객체는 HTTP DTO를 알면 안 된다.
- infra JPA Entity는 domain 객체로 변환해서 domain 바깥으로 누출하지 않는다.

변환 위치:

- Controller: HTTP DTO -> command, result -> HTTP response
- Infra repository: JPA Entity <-> domain object
- Domain service: command -> domain object 생성

## 추상화 기준

필수 추상화:

- UseCase interface
- Repository interface
- 외부 API client interface가 필요한 경우

과한 추상화는 피한다:

- 모든 작은 로직마다 interface 만들기
- Mapper 계층을 무조건 분리하기
- CQRS, Event, Saga를 초기부터 도입하기
- port/adapter 패키지를 과하게 쪼개기

## 테스트 기준

우선순위:

1. domain service/usecase 구현체 단위 테스트
2. domain entity/policy 규칙 테스트
3. repository 구현 통합 테스트
4. controller web 테스트

검증 명령:

```bash
./gradlew test
./gradlew compileKotlin
```

문서만 변경한 경우 Gradle 테스트는 필수 아님.
