package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface VendorProductJpaRepository : JpaRepository<VendorProductJpaEntity, UUID> {
    fun findByIdAndVendorIdAndDeletedAtIsNull(
        id: UUID,
        vendorId: UUID,
    ): VendorProductJpaEntity?
    fun findAllByVendorIdAndDeletedAtIsNullOrderByCreatedAtDesc(
        vendorId: UUID,
        pageable: Pageable,
    ): Page<VendorProductJpaEntity>
}
