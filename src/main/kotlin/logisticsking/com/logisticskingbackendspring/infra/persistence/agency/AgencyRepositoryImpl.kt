package logisticsking.com.logisticskingbackendspring.infra.persistence.agency

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AgencyRepositoryImpl(
    private val jpaRepository: AgencyJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : AgencyRepository {
    private val agency = QAgencyJpaEntity.agencyJpaEntity

    override fun save(agency: Agency): Agency {
        return jpaRepository.save(AgencyJpaEntity.from(agency)).toDomain()
    }

    override fun findById(id: UUID): Agency? {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)?.toDomain()
    }

    override fun findAllByIds(ids: Collection<UUID>): List<Agency> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        return queryFactory
            .selectFrom(agency)
            .where(
                agency.id.`in`(ids),
                agency.deletedAt.isNull,
            )
            .fetch()
            .map(AgencyJpaEntity::toDomain)
    }

    override fun findAll(
        condition: AgencySearchCondition,
        pageable: Pageable,
    ): Page<Agency> {
        val content = queryFactory
            .selectFrom(agency)
            .where(
                agency.deletedAt.isNull,
                condition.normalizedAgencyName?.let { agency.agencyName.containsIgnoreCase(it) },
                condition.normalizedRegion?.let(::regionContains),
                condition.carrier?.let { agency.carrier.eq(it) },
                condition.saturdayDeliveryAvailable?.let { agency.saturdayDeliveryAvailable.eq(it) },
                condition.returnAvailable?.let { agency.returnAvailable.eq(it) },
            )
            .orderBy(agency.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(AgencyJpaEntity::toDomain)

        val total = queryFactory
            .select(agency.count())
            .from(agency)
            .where(
                agency.deletedAt.isNull,
                condition.normalizedAgencyName?.let { agency.agencyName.containsIgnoreCase(it) },
                condition.normalizedRegion?.let(::regionContains),
                condition.carrier?.let { agency.carrier.eq(it) },
                condition.saturdayDeliveryAvailable?.let { agency.saturdayDeliveryAvailable.eq(it) },
                condition.returnAvailable?.let { agency.returnAvailable.eq(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun findAllForRecommendation(): List<Agency> {
        return queryFactory
            .selectFrom(agency)
            .where(agency.deletedAt.isNull)
            .orderBy(agency.createdAt.desc())
            .fetch()
            .map(AgencyJpaEntity::toDomain)
    }

    override fun findByUserId(userId: UUID): Agency? {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId)?.toDomain()
    }

    override fun existsByUserId(userId: UUID): Boolean {
        return jpaRepository.existsByUserId(userId)
    }

    private fun regionContains(region: String): BooleanExpression {
        return agency.mainRegion.containsIgnoreCase(region)
            .or(agency.serviceRegions.containsIgnoreCase(region))
    }
}
