package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VendorProductJpaRepository : JpaRepository<VendorProductJpaEntity, UUID> {
    fun findByIdAndVendorIdAndDeletedAtIsNull(
        id: UUID,
        vendorId: UUID,
    ): VendorProductJpaEntity?
}
