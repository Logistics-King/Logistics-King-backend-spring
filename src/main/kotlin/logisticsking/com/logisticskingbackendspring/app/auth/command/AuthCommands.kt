package logisticsking.com.logisticskingbackendspring.app.auth.command

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

data class SignUpCommand(
    val loginId: String,
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val name: String,
    val role: UserRole,
)

data class LoginCommand(
    val loginId: String,
    val password: String,
)

data class RefreshTokenCommand(
    val refreshToken: String,
)

data class LogoutCommand(
    val refreshToken: String?,
)
