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
        return userJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByLoginId(loginId: String): User? {
        return userJpaRepository.findByLoginId(loginId)?.toDomain()
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
}
