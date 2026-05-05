package logisticsking.com.logisticskingbackendspring.domain.user

import java.util.UUID

class User private constructor(
    val id: UUID,
    val loginId: String,
    val email: String,
    val encodedPassword: String,
    val name: String,
    val role: UserRole,
) {

    companion object {
        fun create(
            id: UUID,
            loginId: String,
            email: String,
            encodedPassword: String,
            name: String,
            role: UserRole,
        ): User {
            return User(
                id = id,
                loginId = loginId,
                email = email,
                encodedPassword = encodedPassword,
                name = name,
                role = role,
            )
        }

        fun restore(
            id: UUID,
            loginId: String,
            email: String,
            encodedPassword: String,
            name: String,
            role: UserRole,
        ): User {
            return User(
                id = id,
                loginId = loginId,
                email = email,
                encodedPassword = encodedPassword,
                name = name,
                role = role,
            )
        }
    }
}
