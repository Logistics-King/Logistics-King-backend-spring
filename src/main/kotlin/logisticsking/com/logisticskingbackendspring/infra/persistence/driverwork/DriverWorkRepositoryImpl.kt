package logisticsking.com.logisticskingbackendspring.infra.persistence.driverwork

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWork
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkRepository
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DriverWorkRepositoryImpl(
    private val jpaRepository: DriverWorkJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : DriverWorkRepository {
    private val driverWork = QDriverWorkJpaEntity.driverWorkJpaEntity

    override fun save(driverWork: DriverWork): DriverWork {
        return jpaRepository.save(DriverWorkJpaEntity.from(driverWork)).toDomain()
    }

    override fun findById(id: UUID): DriverWork? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): DriverWork? {
        return jpaRepository.findByIdAndAgencyId(id, agencyId)?.toDomain()
    }

    override fun findAllByAgencyId(
        agencyId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWork> {
        return findAll(
            pageable = pageable,
            predicates = arrayOf(
                driverWork.agencyId.eq(agencyId),
                condition.status?.let { driverWork.status.eq(it) },
                condition.toServiceRegionPredicate(),
                condition.pickupStartDateFrom?.let { driverWork.pickupStartDate.goe(it) },
                condition.pickupStartDateTo?.let { driverWork.pickupStartDate.loe(it) },
            )
        )
    }

    override fun findOpenByAgencyId(
        agencyId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWork> {
        return findAll(
            pageable = pageable,
            predicates = arrayOf(
                driverWork.agencyId.eq(agencyId),
                driverWork.status.eq(DriverWorkStatus.OPEN),
                condition.toServiceRegionPredicate(),
                condition.pickupStartDateFrom?.let { driverWork.pickupStartDate.goe(it) },
                condition.pickupStartDateTo?.let { driverWork.pickupStartDate.loe(it) },
            )
        )
    }

    override fun findAssignedByDeliverId(
        deliverId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWork> {
        return findAll(
            pageable = pageable,
            predicates = arrayOf(
                driverWork.assignedDeliverId.eq(deliverId),
                condition.status?.let { driverWork.status.eq(it) },
                condition.toServiceRegionPredicate(),
                condition.pickupStartDateFrom?.let { driverWork.pickupStartDate.goe(it) },
                condition.pickupStartDateTo?.let { driverWork.pickupStartDate.loe(it) },
            )
        )
    }

    private fun findAll(
        pageable: Pageable,
        predicates: Array<BooleanExpression?>,
    ): Page<DriverWork> {
        val content = queryFactory
            .selectFrom(driverWork)
            .where(*predicates)
            .orderBy(driverWork.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(DriverWorkJpaEntity::toDomain)

        val total = queryFactory
            .select(driverWork.count())
            .from(driverWork)
            .where(*predicates)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    private fun DriverWorkSearchCondition.toServiceRegionPredicate(): BooleanExpression? {
        return normalizedServiceRegion?.let { driverWork.serviceRegion.containsIgnoreCase(it) }
    }
}
