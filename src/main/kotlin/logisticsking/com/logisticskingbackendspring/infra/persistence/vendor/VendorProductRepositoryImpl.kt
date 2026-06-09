package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProduct
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class VendorProductRepositoryImpl(
    private val jpaRepository: VendorProductJpaRepository,
) : VendorProductRepository {

    override fun save(product: VendorProduct): VendorProduct {
        return jpaRepository.save(VendorProductJpaEntity.from(product)).toDomain()
    }

    override fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): VendorProduct? {
        return jpaRepository.findByIdAndVendorIdAndDeletedAtIsNull(id, vendorId)?.toDomain()
    }

    override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<VendorProduct> {
        return jpaRepository.findAllByVendorIdAndDeletedAtIsNullOrderByCreatedAtDesc(vendorId, pageable)
            .map(VendorProductJpaEntity::toDomain)
    }
}
