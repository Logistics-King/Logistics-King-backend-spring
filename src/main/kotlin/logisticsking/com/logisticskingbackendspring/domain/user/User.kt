package logisticsking.com.logisticskingbackendspring.domain.user

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.util.UUID

class User private constructor(
    val id: UUID,
    val loginId: String,
    val email: String,
    val encodedPassword: String,
    val name: String,
    val role: UserRole,
) {
    fun changePassword(encodedPassword: String): User {
        requireDomain(encodedPassword.isNotBlank(), UserErrorCode.INVALID_PASSWORD)

        return User(
            id = id,
            loginId = loginId,
            email = email,
            encodedPassword = encodedPassword,
            name = name,
            role = role,
        )
    }

    companion object {
        fun create(
            id: UUID,
            loginId: String,
            email: String,
            encodedPassword: String,
            name: String,
            role: UserRole,
        ): User {
            requireDomain(loginId.isNotBlank(), UserErrorCode.INVALID_LOGIN_ID)
            requireDomain(email.isNotBlank(), UserErrorCode.INVALID_EMAIL)
            requireDomain(encodedPassword.isNotBlank(), UserErrorCode.INVALID_PASSWORD)
            requireDomain(name.isNotBlank(), UserErrorCode.INVALID_NAME)

            return User(
                id = id,
                loginId = loginId.trim(),
                email = email.trim(),
                encodedPassword = encodedPassword,
                name = name.trim(),
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
