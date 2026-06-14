package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import com.querydsl.jpa.impl.JPAQueryFactory
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProduct
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductRepository
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductWithVendor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class VendorProductRepositoryImpl(
    private val jpaRepository: ProductJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : VendorProductRepository {
    private val vendorProduct = QProductJpaEntity.productJpaEntity
    private val vendor = QVendorJpaEntity.vendorJpaEntity

    override fun save(product: VendorProduct): VendorProduct {
        return jpaRepository.save(ProductJpaEntity.from(product)).toDomain()
    }

    override fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): VendorProduct? {
        return jpaRepository.findByIdAndVendorIdAndDeletedAtIsNull(id, vendorId)?.toDomain()
    }

    override fun findAllByVendorId(
        vendorId: UUID,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProduct> {
        return findAllByCondition(
            condition = condition,
            pageable = pageable,
            vendorId = vendorId,
        )
    }

    override fun findAll(
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProduct> {
        return findAllByCondition(
            condition = condition,
            pageable = pageable,
            vendorId = null,
        )
    }

    override fun findAllWithVendor(
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProductWithVendor> {
        return findAllWithVendorByCondition(
            condition = condition,
            pageable = pageable,
            nearbyAgency = null,
        )
    }

    override fun findNearbyForAgency(
        agency: Agency,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProduct> {
        val serviceRegions = agency.serviceRegions.ifEmpty { listOf(agency.mainRegion) }

        val content = queryFactory
            .selectFrom(vendorProduct)
            .join(vendor).on(vendorProduct.vendorId.eq(vendor.id))
            .where(
                vendorProduct.deletedAt.isNull,
                vendor.deletedAt.isNull,
                vendor.mainRegion.`in`(serviceRegions),
                condition.normalizedName?.let { vendorProduct.name.containsIgnoreCase(it) },
                condition.category?.let { vendorProduct.category.eq(it) },
                condition.boxSize?.let { vendorProduct.boxSize.eq(it) },
                condition.coldChainType?.let { vendorProduct.coldChainType.eq(it) },
            )
            .orderBy(vendorProduct.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(ProductJpaEntity::toDomain)

        val total = queryFactory
            .select(vendorProduct.count())
            .from(vendorProduct)
            .join(vendor).on(vendorProduct.vendorId.eq(vendor.id))
            .where(
                vendorProduct.deletedAt.isNull,
                vendor.deletedAt.isNull,
                vendor.mainRegion.`in`(serviceRegions),
                condition.normalizedName?.let { vendorProduct.name.containsIgnoreCase(it) },
                condition.category?.let { vendorProduct.category.eq(it) },
                condition.boxSize?.let { vendorProduct.boxSize.eq(it) },
                condition.coldChainType?.let { vendorProduct.coldChainType.eq(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun findNearbyWithVendorForAgency(
        agency: Agency,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProductWithVendor> {
        return findAllWithVendorByCondition(
            condition = condition,
            pageable = pageable,
            nearbyAgency = agency,
        )
    }

    private fun findAllWithVendorByCondition(
        condition: VendorProductSearchCondition,
        pageable: Pageable,
        nearbyAgency: Agency?,
    ): Page<VendorProductWithVendor> {
        val serviceRegions = nearbyAgency?.serviceRegions?.ifEmpty { listOf(nearbyAgency.mainRegion) }

        val content = queryFactory
            .select(vendorProduct, vendor)
            .from(vendorProduct)
            .join(vendor).on(vendorProduct.vendorId.eq(vendor.id))
            .where(
                vendorProduct.deletedAt.isNull,
                vendor.deletedAt.isNull,
                serviceRegions?.let { vendor.mainRegion.`in`(it) },
                condition.normalizedName?.let { vendorProduct.name.containsIgnoreCase(it) },
                condition.category?.let { vendorProduct.category.eq(it) },
                condition.boxSize?.let { vendorProduct.boxSize.eq(it) },
                condition.coldChainType?.let { vendorProduct.coldChainType.eq(it) },
            )
            .orderBy(vendorProduct.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map {
                VendorProductWithVendor(
                    product = it.get(vendorProduct)!!.toDomain(),
                    vendor = it.get(vendor)!!.toDomain(),
                )
            }

        val total = queryFactory
            .select(vendorProduct.count())
            .from(vendorProduct)
            .join(vendor).on(vendorProduct.vendorId.eq(vendor.id))
            .where(
                vendorProduct.deletedAt.isNull,
                vendor.deletedAt.isNull,
                serviceRegions?.let { vendor.mainRegion.`in`(it) },
                condition.normalizedName?.let { vendorProduct.name.containsIgnoreCase(it) },
                condition.category?.let { vendorProduct.category.eq(it) },
                condition.boxSize?.let { vendorProduct.boxSize.eq(it) },
                condition.coldChainType?.let { vendorProduct.coldChainType.eq(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    private fun findAllByCondition(
        condition: VendorProductSearchCondition,
        pageable: Pageable,
        vendorId: UUID?,
    ): Page<VendorProduct> {
        val content = queryFactory
            .selectFrom(vendorProduct)
            .where(
                vendorId?.let { vendorProduct.vendorId.eq(it) },
                vendorProduct.deletedAt.isNull,
                condition.normalizedName?.let { vendorProduct.name.containsIgnoreCase(it) },
                condition.category?.let { vendorProduct.category.eq(it) },
                condition.boxSize?.let { vendorProduct.boxSize.eq(it) },
                condition.coldChainType?.let { vendorProduct.coldChainType.eq(it) },
            )
            .orderBy(vendorProduct.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(ProductJpaEntity::toDomain)

        val total = queryFactory
            .select(vendorProduct.count())
            .from(vendorProduct)
            .where(
                vendorId?.let { vendorProduct.vendorId.eq(it) },
                vendorProduct.deletedAt.isNull,
                condition.normalizedName?.let { vendorProduct.name.containsIgnoreCase(it) },
                condition.category?.let { vendorProduct.category.eq(it) },
                condition.boxSize?.let { vendorProduct.boxSize.eq(it) },
                condition.coldChainType?.let { vendorProduct.coldChainType.eq(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }
}
