package logisticsking.com.logisticskingbackendspring.app.auth.command

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
