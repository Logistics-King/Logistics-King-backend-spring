package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import com.querydsl.jpa.impl.JPAQueryFactory
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProduct
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductRepository
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class VendorProductRepositoryImpl(
    private val jpaRepository: VendorProductJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : VendorProductRepository {
    private val vendorProduct = QVendorProductJpaEntity.vendorProductJpaEntity

    override fun save(product: VendorProduct): VendorProduct {
        return jpaRepository.save(VendorProductJpaEntity.from(product)).toDomain()
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
        val content = queryFactory
            .selectFrom(vendorProduct)
            .where(
                vendorProduct.vendorId.eq(vendorId),
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
            .map(VendorProductJpaEntity::toDomain)

        val total = queryFactory
            .select(vendorProduct.count())
            .from(vendorProduct)
            .where(
                vendorProduct.vendorId.eq(vendorId),
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
