package logisticsking.com.logisticskingbackendspring.infra.persistence.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    val loginId: String,

    @Column(name = "email", nullable = false, unique = true, length = 255)
    val email: String,

    @Column(name = "encoded_password", nullable = false, length = 255)
    val encodedPassword: String,

    @Column(name = "name", nullable = false, length = 50)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    val role: UserRole,
) : BaseJpaEntity() {

    fun toDomain(): User {
        return User.restore(
            id = id,
            loginId = loginId,
            email = email,
            encodedPassword = encodedPassword,
            name = name,
            role = role,
        )
    }

    companion object {
        fun from(user: User): UserJpaEntity {
            return UserJpaEntity(
                id = user.id,
                loginId = user.loginId,
                email = user.email,
                encodedPassword = user.encodedPassword,
                name = user.name,
                role = user.role,
            )
        }
    }
}
