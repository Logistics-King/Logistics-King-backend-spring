package logisticsking.com.logisticskingbackendspring.app.auth.dto

import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand

sealed interface AuthRequest {
    data class Login(
        val loginId: String,
        val password: String,
    ) : AuthRequest {
        fun toCommand(): LoginCommand {
            return LoginCommand(
                loginId = loginId,
                password = password,
            )
        }
    }
}
