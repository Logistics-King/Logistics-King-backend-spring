package logisticsking.com.logisticskingbackendspring.domain.user

import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): User?
    fun findByLoginId(loginId: String): User?
    fun existsByLoginId(loginId: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun save(user: User): User
}
