package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.data.jpa.repository.JpaRepository

interface EndPointJpaRepository : JpaRepository<EndPointJpaEntity, EndPointJpaEntityId> {
    fun findByRole(role: UserRole): List<EndPointJpaEntity>
    fun existsByUrlAndRole(
        url: String,
        role: UserRole,
    ): Boolean
}
