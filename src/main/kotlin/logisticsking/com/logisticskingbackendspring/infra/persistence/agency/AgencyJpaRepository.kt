package logisticsking.com.logisticskingbackendspring.infra.persistence.agency

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AgencyJpaRepository : JpaRepository<AgencyJpaEntity, UUID> {
    fun findByUserId(userId: UUID): AgencyJpaEntity?
    fun existsByUserId(userId: UUID): Boolean
}
