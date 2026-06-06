package logisticsking.com.logisticskingbackendspring.infra.persistence.agency

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AgencyJpaRepository : JpaRepository<AgencyJpaEntity, UUID> {
    fun findByIdAndDeletedAtIsNull(id: UUID): AgencyJpaEntity?
    fun findByUserIdAndDeletedAtIsNull(userId: UUID): AgencyJpaEntity?
    fun existsByUserId(userId: UUID): Boolean
}
