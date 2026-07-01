package logisticsking.com.logisticskingbackendspring.infra.persistence.deliver

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverRepository
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DeliverRepositoryImpl(
    private val jpaRepository: DeliverJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : DeliverRepository {
    private val deliver = QDeliverJpaEntity.deliverJpaEntity

    override fun save(deliver: Deliver): Deliver {
        return jpaRepository.save(DeliverJpaEntity.from(deliver)).toDomain()
    }

    override fun findById(id: UUID): Deliver? {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)?.toDomain()
    }

    override fun findAllByIds(ids: Collection<UUID>): List<Deliver> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        return queryFactory
            .selectFrom(deliver)
            .where(
                deliver.id.`in`(ids),
                deliver.deletedAt.isNull,
            )
            .fetch()
            .map(DeliverJpaEntity::toDomain)
    }

    override fun findByUserId(userId: UUID): Deliver? {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId)?.toDomain()
    }

    override fun findAllByAgencyId(
        agencyId: UUID,
        condition: DeliverSearchCondition,
        pageable: Pageable,
    ): Page<Deliver> {
        val content = queryFactory
            .selectFrom(deliver)
            .where(
                deliver.agencyId.eq(agencyId),
                deliver.deletedAt.isNull,
                condition.active?.let { deliver.active.eq(it) },
                condition.normalizedServiceRegion?.let(::serviceRegionsContains),
                condition.normalizedDriverName?.let { deliver.driverName.containsIgnoreCase(it) },
                condition.normalizedVehicleNumber?.let { deliver.vehicleNumber.containsIgnoreCase(it) },
            )
            .orderBy(deliver.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(DeliverJpaEntity::toDomain)

        val total = queryFactory
            .select(deliver.count())
            .from(deliver)
            .where(
                deliver.agencyId.eq(agencyId),
                deliver.deletedAt.isNull,
                condition.active?.let { deliver.active.eq(it) },
                condition.normalizedServiceRegion?.let(::serviceRegionsContains),
                condition.normalizedDriverName?.let { deliver.driverName.containsIgnoreCase(it) },
                condition.normalizedVehicleNumber?.let { deliver.vehicleNumber.containsIgnoreCase(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun existsByUserId(userId: UUID): Boolean {
        return jpaRepository.existsByUserId(userId)
    }

    private fun serviceRegionsContains(serviceRegion: String): BooleanExpression {
        return deliver.serviceRegions.containsIgnoreCase(serviceRegion)
    }
}
