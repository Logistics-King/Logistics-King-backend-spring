package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import logisticsking.com.logisticskingbackendspring.domain.vendor.Product
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductRepository
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ProductRepositoryImpl(
    private val jpaRepository: ProductJpaRepository,
    private val queryFactory: JPAQueryFactory,
) : ProductRepository {
    private val product = QProductJpaEntity.productJpaEntity

    override fun save(product: Product): Product {
        return jpaRepository.save(ProductJpaEntity.from(product)).toDomain()
    }

    override fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): Product? {
        return jpaRepository.findByIdAndVendorIdAndDeletedAtIsNull(id, vendorId)?.toDomain()
    }

    override fun findAllByVendorId(
        vendorId: UUID,
        condition: ProductSearchCondition,
        pageable: Pageable,
    ): Page<Product> {
        return findAllByCondition(
            condition = condition,
            pageable = pageable,
            vendorId = vendorId,
        )
    }

    override fun findAllByIdsAndVendorIdForUpdate(
        ids: Collection<UUID>,
        vendorId: UUID,
    ): List<Product> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        return queryFactory
            .selectFrom(product)
            .where(
                product.id.`in`(ids),
                product.vendorId.eq(vendorId),
                product.deletedAt.isNull,
            )
            .orderBy(product.id.asc())
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch()
            .map(ProductJpaEntity::toDomain)
    }

    private fun findAllByCondition(
        condition: ProductSearchCondition,
        pageable: Pageable,
        vendorId: UUID?,
    ): Page<Product> {
        val content = queryFactory
            .selectFrom(product)
            .where(
                vendorId?.let { product.vendorId.eq(it) },
                product.deletedAt.isNull,
                condition.normalizedName?.let { product.name.containsIgnoreCase(it) },
                condition.category?.let { product.category.eq(it) },
                condition.boxSize?.let { product.boxSize.eq(it) },
                condition.coldChainType?.let { product.coldChainType.eq(it) },
            )
            .orderBy(product.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
            .map(ProductJpaEntity::toDomain)

        val total = queryFactory
            .select(product.count())
            .from(product)
            .where(
                vendorId?.let { product.vendorId.eq(it) },
                product.deletedAt.isNull,
                condition.normalizedName?.let { product.name.containsIgnoreCase(it) },
                condition.category?.let { product.category.eq(it) },
                condition.boxSize?.let { product.boxSize.eq(it) },
                condition.coldChainType?.let { product.coldChainType.eq(it) },
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }
}
