package logisticsking.com.logisticskingbackendspring.domain.user

import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): User?
    fun findByLoginId(loginId: String): User?
    fun findByNameAndEmail(
        name: String,
        email: String,
    ): User?

    fun findByLoginIdAndEmail(
        loginId: String,
        email: String,
    ): User?

    fun existsByLoginId(loginId: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun save(user: User): User

    fun updatePassword(
        id: UUID,
        encodedPassword: String,
    ): User?
}
