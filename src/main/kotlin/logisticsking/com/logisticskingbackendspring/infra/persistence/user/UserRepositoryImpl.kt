package logisticsking.com.logisticskingbackendspring.infra.persistence.user

import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {

    override fun findById(id: UUID): User? {
        return userJpaRepository.findByIdAndDeletedAtIsNull(id)?.toDomain()
    }

    override fun findByLoginId(loginId: String): User? {
        return userJpaRepository.findByLoginIdAndDeletedAtIsNull(loginId)?.toDomain()
    }

    override fun findByNameAndEmail(
        name: String,
        email: String,
    ): User? {
        return userJpaRepository.findByNameAndEmailAndDeletedAtIsNull(name, email)?.toDomain()
    }

    override fun findByLoginIdAndEmail(
        loginId: String,
        email: String,
    ): User? {
        return userJpaRepository.findByLoginIdAndEmailAndDeletedAtIsNull(loginId, email)?.toDomain()
    }

    override fun existsByLoginId(loginId: String): Boolean {
        return userJpaRepository.existsByLoginId(loginId)
    }

    override fun existsByEmail(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }

    override fun save(user: User): User {
        return userJpaRepository.save(UserJpaEntity.from(user)).toDomain()
    }

    override fun updatePassword(
        id: UUID,
        encodedPassword: String,
    ): User? {
        val updatedCount = userJpaRepository.updatePassword(id, encodedPassword)
        if (updatedCount == 0) {
            return null
        }

        return findById(id)
    }
}
