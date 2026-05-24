package logisticsking.com.logisticskingbackendspring.app.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand

@Schema(description = "인증 요청")
sealed interface AuthRequest {
    @Schema(description = "로그인 요청")
    data class Login(
        @field:Schema(description = "로그인 ID", example = "vendor01")
        val loginId: String,
        @field:Schema(description = "비밀번호", example = "password1234")
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
