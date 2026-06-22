package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ContractRequestQueryRepository(
    private val queryFactory: JPAQueryFactory,
) {
    private val contractRequest = QContractRequestJpaEntity.contractRequestJpaEntity
    private val contractRequestItem = QContractRequestItemJpaEntity.contractRequestItemJpaEntity

    fun findByIdForUpdate(id: UUID): ContractRequestJpaEntity? {
        return queryFactory
            .selectFrom(contractRequest)
            .where(contractRequest.id.eq(id))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }

    fun findByIdAndVendorIdForUpdate(
        id: UUID,
        vendorId: UUID,
    ): ContractRequestJpaEntity? {
        return queryFactory
            .selectFrom(contractRequest)
            .where(
                contractRequest.id.eq(id),
                contractRequest.type.eq(ContractRequestType.VENDOR_OFFER),
                contractRequest.requesterType.eq(ContractPartyType.VENDOR),
                contractRequest.requesterId.eq(vendorId),
            )
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }

    fun findByIdAndRequesterForUpdate(
        id: UUID,
        requesterType: ContractPartyType,
        requesterId: UUID,
    ): ContractRequestJpaEntity? {
        return queryFactory
            .selectFrom(contractRequest)
            .where(
                contractRequest.id.eq(id),
                contractRequest.requesterType.eq(requesterType),
                contractRequest.requesterId.eq(requesterId),
            )
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }

    fun findByIdAndApproverForUpdate(
        id: UUID,
        approverType: ContractPartyType,
        approverId: UUID,
    ): ContractRequestJpaEntity? {
        return queryFactory
            .selectFrom(contractRequest)
            .where(
                contractRequest.id.eq(id),
                contractRequest.approverType.eq(approverType),
                contractRequest.approverId.eq(approverId),
            )
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }

    fun findAllByRequester(
        requesterType: ContractPartyType,
        requesterId: UUID,
        condition: ContractRequestSearchCondition,
        pageable: Pageable,
    ): Page<ContractRequestJpaEntity> {
        val content = queryFactory
            .selectFrom(contractRequest)
            .where(
                contractRequest.requesterType.eq(requesterType),
                contractRequest.requesterId.eq(requesterId),
                condition.status?.let { contractRequest.status.eq(it) },
                condition.normalizedPickupRegion?.let { contractRequest.pickupRegion.containsIgnoreCase(it) },
                condition.saturdayDeliveryRequired?.let { contractRequest.saturdayDeliveryRequired.eq(it) },
                condition.returnRequired?.let { contractRequest.returnRequired.eq(it) },
                condition.normalizedProductName?.let(::productNameMatches),
                condition.productCategory?.let(::productCategoryMatches),
                condition.boxSize?.let(::boxSizeMatches),
                condition.coldChainType?.let(::coldChainTypeMatches),
            )
            .orderBy(contractRequest.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .select(contractRequest.count())
            .from(contractRequest)
            .where(
                contractRequest.requesterType.eq(requesterType),
                contractRequest.requesterId.eq(requesterId),
                condition.status?.let { contractRequest.status.eq(it) },
                condition.normalizedPickupRegion?.let { contractRequest.pickupRegion.containsIgnoreCase(it) },
                condition.saturdayDeliveryRequired?.let { contractRequest.saturdayDeliveryRequired.eq(it) },
                condition.returnRequired?.let { contractRequest.returnRequired.eq(it) },
                condition.normalizedProductName?.let(::productNameMatches),
                condition.productCategory?.let(::productCategoryMatches),
                condition.boxSize?.let(::boxSizeMatches),
                condition.coldChainType?.let(::coldChainTypeMatches),
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    fun findOpenVendorOffersForAgency(
        agencyId: UUID,
        pageable: Pageable,
    ): Page<ContractRequestJpaEntity> {
        val content = queryFactory
            .selectFrom(contractRequest)
            .where(
                contractRequest.type.eq(ContractRequestType.VENDOR_OFFER),
                contractRequest.status.eq(ContractRequestStatus.OPEN),
                contractRequest.approverType.eq(ContractPartyType.AGENCY),
                contractRequest.approverId.isNull.or(contractRequest.approverId.eq(agencyId)),
            )
            .orderBy(contractRequest.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .select(contractRequest.count())
            .from(contractRequest)
            .where(
                contractRequest.type.eq(ContractRequestType.VENDOR_OFFER),
                contractRequest.status.eq(ContractRequestStatus.OPEN),
                contractRequest.approverType.eq(ContractPartyType.AGENCY),
                contractRequest.approverId.isNull.or(contractRequest.approverId.eq(agencyId)),
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    fun existsActiveByVendorIdAndProductIds(
        vendorId: UUID,
        productIds: Collection<UUID>,
        excludedContractRequestId: UUID?,
    ): Boolean {
        if (productIds.isEmpty()) {
            return false
        }

        val id = queryFactory
            .select(contractRequest.id)
            .from(contractRequest)
            .leftJoin(contractRequestItem)
            .on(contractRequest.id.eq(contractRequestItem.contractRequestId))
            .where(
                excludedContractRequestId?.let { contractRequest.id.ne(it) },
                contractRequest.status.`in`(ACTIVE_PRODUCT_LOCK_STATUSES),
                contractRequest.requesterType.eq(ContractPartyType.VENDOR)
                    .and(contractRequest.requesterId.eq(vendorId))
                    .or(
                        contractRequest.approverType.eq(ContractPartyType.VENDOR)
                            .and(contractRequest.approverId.eq(vendorId))
                    ),
                contractRequest.productId.`in`(productIds)
                    .or(contractRequestItem.productId.`in`(productIds)),
            )
            .limit(1)
            .fetchFirst()

        return id != null
    }

    private fun productNameMatches(productName: String): BooleanExpression {
        return contractRequest.productName.containsIgnoreCase(productName)
            .or(
                JPAExpressions
                    .selectOne()
                    .from(contractRequestItem)
                    .where(
                        contractRequestItem.contractRequestId.eq(contractRequest.id),
                        contractRequestItem.productName.containsIgnoreCase(productName),
                    )
                    .exists()
            )
    }

    private fun productCategoryMatches(productCategory: ProductCategory): BooleanExpression {
        return contractRequest.productCategory.eq(productCategory)
            .or(
                JPAExpressions
                    .selectOne()
                    .from(contractRequestItem)
                    .where(
                        contractRequestItem.contractRequestId.eq(contractRequest.id),
                        contractRequestItem.productCategory.eq(productCategory),
                    )
                    .exists()
            )
    }

    private fun boxSizeMatches(boxSize: BoxSize): BooleanExpression {
        return contractRequest.boxSize.eq(boxSize)
            .or(
                JPAExpressions
                    .selectOne()
                    .from(contractRequestItem)
                    .where(
                        contractRequestItem.contractRequestId.eq(contractRequest.id),
                        contractRequestItem.boxSize.eq(boxSize),
                    )
                    .exists()
            )
    }

    private fun coldChainTypeMatches(coldChainType: ColdChainType): BooleanExpression {
        return contractRequest.coldChainType.eq(coldChainType)
            .or(
                JPAExpressions
                    .selectOne()
                    .from(contractRequestItem)
                    .where(
                        contractRequestItem.contractRequestId.eq(contractRequest.id),
                        contractRequestItem.coldChainType.eq(coldChainType),
                    )
                    .exists()
            )
    }

    companion object {
        private val ACTIVE_PRODUCT_LOCK_STATUSES = listOf(
            ContractRequestStatus.OPEN,
            ContractRequestStatus.CONTRACTED,
        )
    }
}
