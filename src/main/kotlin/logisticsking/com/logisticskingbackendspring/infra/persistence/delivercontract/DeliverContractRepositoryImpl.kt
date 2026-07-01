package logisticsking.com.logisticskingbackendspring.infra.persistence.delivercontract

import com.querydsl.jpa.impl.JPAQueryFactory
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContract
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractRepository
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DeliverContractRepositoryImpl(
    private val jpaRepository: DeliverContractJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : DeliverContractRepository {
    private val deliverContract = QDeliverContractJpaEntity.deliverContractJpaEntity

    override fun save(deliverContract: DeliverContract): DeliverContract {
        return jpaRepository.save(DeliverContractJpaEntity.from(deliverContract)).toDomain()
    }

    override fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): DeliverContract? {
        return jpaRepository.findByIdAndAgencyId(id, agencyId)?.toDomain()
    }

    override fun findByIdAndDeliverId(
        id: UUID,
        deliverId: UUID,
    ): DeliverContract? {
        return jpaRepository.findByIdAndDeliverId(id, deliverId)?.toDomain()
    }

    override fun findAllByAgencyId(
        agencyId: UUID,
        condition: DeliverContractSearchCondition,
        pageable: Pageable,
    ): Page<DeliverContract> {
        val content = queryFactory
            .selectFrom(deliverContract)
            .where(
                deliverContract.agencyId.eq(agencyId),
                condition.status?.let { deliverContract.status.eq(it) },
                condition.normalizedServiceRegion?.let { deliverContract.serviceRegion.containsIgnoreCase(it) },
                condition.startDateFrom?.let { deliverContract.startDate.goe(it) },
                condition.startDateTo?.let { deliverContract.startDate.loe(it) },
            )
            .orderBy(deliverContract.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(DeliverContractJpaEntity::toDomain)

        val total = queryFactory
            .select(deliverContract.count())
            .from(deliverContract)
            .where(
                deliverContract.agencyId.eq(agencyId),
                condition.status?.let { deliverContract.status.eq(it) },
                condition.normalizedServiceRegion?.let { deliverContract.serviceRegion.containsIgnoreCase(it) },
                condition.startDateFrom?.let { deliverContract.startDate.goe(it) },
                condition.startDateTo?.let { deliverContract.startDate.loe(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun findAllByDeliverId(
        deliverId: UUID,
        condition: DeliverContractSearchCondition,
        pageable: Pageable,
    ): Page<DeliverContract> {
        val content = queryFactory
            .selectFrom(deliverContract)
            .where(
                deliverContract.deliverId.eq(deliverId),
                condition.status?.let { deliverContract.status.eq(it) },
                condition.normalizedServiceRegion?.let { deliverContract.serviceRegion.containsIgnoreCase(it) },
                condition.startDateFrom?.let { deliverContract.startDate.goe(it) },
                condition.startDateTo?.let { deliverContract.startDate.loe(it) },
            )
            .orderBy(deliverContract.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(DeliverContractJpaEntity::toDomain)

        val total = queryFactory
            .select(deliverContract.count())
            .from(deliverContract)
            .where(
                deliverContract.deliverId.eq(deliverId),
                condition.status?.let { deliverContract.status.eq(it) },
                condition.normalizedServiceRegion?.let { deliverContract.serviceRegion.containsIgnoreCase(it) },
                condition.startDateFrom?.let { deliverContract.startDate.goe(it) },
                condition.startDateTo?.let { deliverContract.startDate.loe(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun existsActiveByAgencyIdAndDeliverId(
        agencyId: UUID,
        deliverId: UUID,
    ): Boolean {
        return jpaRepository.existsByAgencyIdAndDeliverIdAndStatusIn(
            agencyId = agencyId,
            deliverId = deliverId,
            statuses = ACTIVE_STATUSES,
        )
    }

    private companion object {
        private val ACTIVE_STATUSES = setOf(
            DeliverContractStatus.REQUESTED,
            DeliverContractStatus.ACCEPTED,
        )
    }
}
