# Querydsl 작성 규칙

## 목표

Querydsl을 사용할 때 조건, projection, join, paging, aggregate 쿼리를 일관된 형태로 작성한다.

현재 프로젝트에는 Querydsl 설정이 아직 없다. 이 문서는 Querydsl 도입 후 기본 API만 사용하는 기준으로 작성한다.

## 기본 Where 조건

동적 조건은 `?.let`을 사용해 null 조건을 제외한다.

```kotlin
fun search(condition: SearchCondition): List<ContractRequestJpaEntity> {
    return queryFactory
        .selectFrom(contractRequest)
        .where(
            condition.shipperId?.let { contractRequest.shipperId.eq(it) },
            condition.pickupRegion?.let { contractRequest.pickupRegion.eq(it) },
            condition.status?.let { contractRequest.status.eq(it) },
            condition.fromCreatedAt?.let { contractRequest.createdAt.goe(it) },
            condition.toCreatedAt?.let { contractRequest.createdAt.loe(it) },
            condition.minMonthlyVolume?.let { contractRequest.monthlyVolume.goe(it) },
        )
        .orderBy(contractRequest.createdAt.desc())
        .fetch()
}
```

규칙:

- `BooleanBuilder`는 복잡한 OR 조건이 필요할 때만 사용한다.
- 단순 AND 검색은 `.where(condition?.let { ... })` 형태를 우선한다.
- null 조건을 빈 문자열이나 임의 기본값으로 바꾸지 않는다.

## Projection

조회 전용 응답은 필요한 필드만 projection한다.

Constructor projection을 기본으로 사용한다.

```kotlin
data class ContractRequestSummary(
    val id: Long,
    val shipperId: Long,
    val pickupRegion: String,
    val monthlyVolume: Int,
    val status: ContractRequestStatus,
)

fun findSummaries(shipperId: Long): List<ContractRequestSummary> {
    return queryFactory
        .select(
            Projections.constructor(
                ContractRequestSummary::class.java,
                contractRequest.id,
                contractRequest.shipperId,
                contractRequest.pickupRegion,
                contractRequest.monthlyVolume,
                contractRequest.status,
            )
        )
        .from(contractRequest)
        .where(contractRequest.shipperId.eq(shipperId))
        .fetch()
}
```

Fields projection은 기본 생성자나 mutable field가 필요한 DTO에서만 사용한다.

```kotlin
fun findSummariesByFields(shipperId: Long): List<ContractRequestSummary> {
    return queryFactory
        .select(
            Projections.fields(
                ContractRequestSummary::class.java,
                contractRequest.id,
                contractRequest.shipperId,
                contractRequest.pickupRegion,
                contractRequest.monthlyVolume,
                contractRequest.status,
            )
        )
        .from(contractRequest)
        .where(contractRequest.shipperId.eq(shipperId))
        .fetch()
}
```

## Join

명시적인 조인 조건을 사용한다.

```kotlin
fun findProposalWithAgency(proposalId: Long): ProposalWithAgency? {
    return queryFactory
        .select(
            Projections.constructor(
                ProposalWithAgency::class.java,
                proposal.id,
                proposal.unitPrice,
                proposal.saturdayDeliveryAvailable,
                agency.name,
                agency.carrier,
            )
        )
        .from(proposal)
        .join(agency).on(proposal.agencyId.eq(agency.id))
        .where(proposal.id.eq(proposalId))
        .fetchOne()
}
```

Optional relation은 left join을 사용한다.

```kotlin
fun findContractWithSelectedProposal(contractId: Long): ContractDetail? {
    return queryFactory
        .select(
            Projections.constructor(
                ContractDetail::class.java,
                contract.id,
                contract.contractRequestId,
                proposal.id,
                proposal.unitPrice,
            )
        )
        .from(contract)
        .leftJoin(proposal).on(contract.selectedProposalId.eq(proposal.id))
        .where(contract.id.eq(contractId))
        .fetchOne()
}
```

## Paging

content query와 count query를 분리한다.

```kotlin
fun findPage(
    condition: ContractRequestSearchCondition,
    pageable: Pageable,
): Page<ContractRequestJpaEntity> {
    val content = queryFactory
        .selectFrom(contractRequest)
        .where(
            condition.shipperId?.let { contractRequest.shipperId.eq(it) },
            condition.status?.let { contractRequest.status.eq(it) },
            condition.pickupRegion?.let { contractRequest.pickupRegion.eq(it) },
        )
        .orderBy(contractRequest.createdAt.desc())
        .offset(pageable.offset)
        .limit(pageable.pageSize.toLong())
        .fetch()

    val total = queryFactory
        .select(contractRequest.count())
        .from(contractRequest)
        .where(
            condition.shipperId?.let { contractRequest.shipperId.eq(it) },
            condition.status?.let { contractRequest.status.eq(it) },
            condition.pickupRegion?.let { contractRequest.pickupRegion.eq(it) },
        )
        .fetchOne() ?: 0L

    return PageImpl(content, pageable, total)
}
```

규칙:

- 정렬은 명확하게 지정한다.
- 큰 테이블에서는 count query 최적화 필요 여부를 검토한다.
- page content가 domain으로 변환되어야 하면 repository 구현체에서 변환한다.

## Aggregate

집계 결과는 projection DTO로 받는다.

```kotlin
data class ProposalStatistics(
    val totalCount: Long,
    val minUnitPrice: Int,
    val maxUnitPrice: Int,
    val avgUnitPrice: Double,
)

fun getProposalStatistics(contractRequestId: Long): ProposalStatistics {
    return queryFactory
        .select(
            Projections.constructor(
                ProposalStatistics::class.java,
                proposal.count(),
                proposal.unitPrice.min(),
                proposal.unitPrice.max(),
                proposal.unitPrice.avg(),
            )
        )
        .from(proposal)
        .where(proposal.contractRequestId.eq(contractRequestId))
        .fetchOne() ?: ProposalStatistics(0, 0, 0, 0.0)
}
```

## Group By

그룹 기준과 정렬 기준을 명확히 맞춘다.

```kotlin
data class DailyContractRequestStatistics(
    val date: LocalDate,
    val count: Long,
)

fun getDailyStatistics(
    fromDate: LocalDate,
    toDate: LocalDate,
): List<DailyContractRequestStatistics> {
    val dateExpression = contractRequest.createdAt.date()

    return queryFactory
        .select(
            Projections.constructor(
                DailyContractRequestStatistics::class.java,
                dateExpression,
                contractRequest.count(),
            )
        )
        .from(contractRequest)
        .where(
            contractRequest.createdAt.goe(fromDate.atStartOfDay()),
            contractRequest.createdAt.lt(toDate.plusDays(1).atStartOfDay()),
        )
        .groupBy(dateExpression)
        .orderBy(dateExpression.asc())
        .fetch()
}
```

## Fetch Join

N+1 문제가 발생하는 조회에서는 fetch join을 검토한다.

```kotlin
fun findWithProposals(contractRequestId: Long): ContractRequestJpaEntity? {
    return queryFactory
        .selectFrom(contractRequest)
        .leftJoin(contractRequest.proposals, proposal).fetchJoin()
        .where(contractRequest.id.eq(contractRequestId))
        .fetchOne()
}
```

주의:

- 컬렉션 fetch join과 paging을 함께 쓰지 않는다.
- 조회 전용 화면에서는 fetch join보다 projection이 더 단순할 수 있다.
- domain 객체 반환이 필요하면 infra repository에서 JPA Entity를 domain으로 변환한다.

## 주의

- 조회에 필요한 필드만 projection한다.
- N+1 가능성이 있는 연관 조회는 fetch join 또는 projection으로 해결한다.
- query code는 `infra.persistence` 아래 repository 구현체에 둔다.
- domain service나 app controller에 Querydsl 코드를 두지 않는다.
