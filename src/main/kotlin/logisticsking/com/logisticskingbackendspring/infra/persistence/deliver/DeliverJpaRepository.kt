package logisticsking.com.logisticskingbackendspring.infra.persistence.deliver

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeliverJpaRepository : JpaRepository<DeliverJpaEntity, UUID> {
    fun findByUserId(userId: UUID): DeliverJpaEntity?
    fun existsByUserId(userId: UUID): Boolean
}
