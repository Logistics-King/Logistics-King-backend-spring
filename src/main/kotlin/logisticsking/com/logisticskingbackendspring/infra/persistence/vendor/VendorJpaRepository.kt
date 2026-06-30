package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VendorJpaRepository : JpaRepository<VendorJpaEntity, UUID> {
    fun findByIdAndDeletedAtIsNull(id: UUID): VendorJpaEntity?
    fun findAllByIdInAndDeletedAtIsNull(ids: Collection<UUID>): List<VendorJpaEntity>
    fun findAllByDeletedAtIsNull(): List<VendorJpaEntity>
    fun findByUserIdAndDeletedAtIsNull(userId: UUID): VendorJpaEntity?
    fun existsByUserId(userId: UUID): Boolean
}
