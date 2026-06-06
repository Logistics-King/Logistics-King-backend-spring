package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProduct
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductRepository
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

    override fun findAllByVendorId(vendorId: UUID): List<VendorProduct> {
        return jpaRepository.findAllByVendorIdAndDeletedAtIsNull(vendorId).map(VendorProductJpaEntity::toDomain)
    }
}
