package logisticsking.com.logisticskingbackendspring.app.auth.result

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.util.UUID

data class SignUpResult(
    val userId: UUID,
    val role: UserRole,
)

data class LoginResult(
    val userId: UUID,
    val role: UserRole,
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshTokenResult(
    val userId: UUID,
    val role: UserRole,
    val accessToken: String,
    val refreshToken: String,
)

data class LogoutResult(
    val loggedOut: Boolean,
)
