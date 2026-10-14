package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.RecentPublicContract
import logisticsking.com.logisticskingbackendspring.infra.persistence.agency.QAgencyJpaEntity
import logisticsking.com.logisticskingbackendspring.infra.persistence.vendor.QVendorJpaEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PublicContractQueryRepository(
    private val queryFactory: JPAQueryFactory,
) {
    private val contract = QContractJpaEntity.contractJpaEntity
    private val vendor = QVendorJpaEntity.vendorJpaEntity
    private val agency = QAgencyJpaEntity.agencyJpaEntity

    fun findRecent(
        region: String?,
        createdSince: LocalDateTime,
        limit: Int,
    ): List<RecentPublicContract> {
        return queryFactory
            .select(
                Projections.constructor(
                    RecentPublicContract::class.java,
                    vendor.businessName,
                    agency.agencyName,
                    contract.pickupRegion,
                    contract.monthlyVolume,
                    contract.productCategory,
                    contract.boxSize,
                    contract.unitPrice,
                    contract.pickupStartTime,
                    contract.pickupEndTime,
                    contract.saturdayDeliveryAvailable,
                    contract.returnAvailable,
                    contract.coldChainType,
                    contract.createdAt,
                )
            )
            .from(contract)
            .join(vendor).on(contract.vendorId.eq(vendor.id))
            .join(agency).on(contract.agencyId.eq(agency.id))
            .where(
                contract.status.eq(ContractStatus.ACTIVE),
                vendor.deletedAt.isNull,
                agency.deletedAt.isNull,
                region?.trim()?.takeIf(String::isNotBlank)?.let { contract.pickupRegion.containsIgnoreCase(it) },
                contract.createdAt.goe(createdSince),
            )
            .orderBy(contract.createdAt.desc())
            .limit(limit.toLong())
            .fetch()
    }
}
