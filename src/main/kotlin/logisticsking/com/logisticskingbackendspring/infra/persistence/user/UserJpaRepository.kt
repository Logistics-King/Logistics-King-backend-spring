package logisticsking.com.logisticskingbackendspring.infra.persistence.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    fun findByLoginId(loginId: String): UserJpaEntity?
    fun existsByLoginId(loginId: String): Boolean
    fun existsByEmail(email: String): Boolean
}
