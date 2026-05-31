package logisticsking.com.logisticskingbackendspring.app.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpCommand
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

@Schema(description = "인증 요청")
sealed interface AuthRequest {
    data class SignUpVendor(
        @field:Schema(description = "로그인 ID", example = "vendor01")
        val loginId: String,
        @field:Schema(description = "이메일", example = "vendor01@example.com")
        val email: String,
        @field:Schema(description = "비밀번호", example = "password1234")
        val password: String,
        @field:Schema(description = "이름", example = "안산 옷가게")
        val name: String,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId,
                email = email,
                password = password,
                name = name,
                role = UserRole.VENDOR,
            )
        }
    }

    data class SignUpAgency(
        @field:Schema(description = "로그인 ID", example = "agency01")
        val loginId: String,
        @field:Schema(description = "이메일", example = "agency01@example.com")
        val email: String,
        @field:Schema(description = "비밀번호", example = "password1234")
        val password: String,
        @field:Schema(description = "이름", example = "CJ 일동대리점")
        val name: String,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId,
                email = email,
                password = password,
                name = name,
                role = UserRole.AGENCY,
            )
        }
    }

    data class SignUpDriver(
        @field:Schema(description = "로그인 ID", example = "driver01")
        val loginId: String,
        @field:Schema(description = "이메일", example = "driver01@example.com")
        val email: String,
        @field:Schema(description = "비밀번호", example = "password1234")
        val password: String,
        @field:Schema(description = "이름", example = "김택배")
        val name: String,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId,
                email = email,
                password = password,
                name = name,
                role = UserRole.DRIVER,
            )
        }
    }

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
