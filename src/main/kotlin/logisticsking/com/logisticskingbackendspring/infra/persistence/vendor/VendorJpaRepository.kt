package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VendorJpaRepository : JpaRepository<VendorJpaEntity, UUID> {
    fun findByUserId(userId: UUID): VendorJpaEntity?
    fun existsByUserId(userId: UUID): Boolean
}
