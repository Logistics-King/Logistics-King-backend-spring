package logisticsking.com.logisticskingbackendspring.infra.persistence.deliver

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeliverJpaRepository : JpaRepository<DeliverJpaEntity, UUID> {
    fun findByIdAndDeletedAtIsNull(id: UUID): DeliverJpaEntity?
    fun findByUserIdAndDeletedAtIsNull(userId: UUID): DeliverJpaEntity?
    fun findAllByAgencyIdAndDeletedAtIsNullOrderByCreatedAtDesc(
        agencyId: UUID,
        pageable: Pageable,
    ): Page<DeliverJpaEntity>
    fun existsByUserId(userId: UUID): Boolean
}
