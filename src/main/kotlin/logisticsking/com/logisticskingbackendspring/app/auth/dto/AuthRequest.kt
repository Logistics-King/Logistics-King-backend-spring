package logisticsking.com.logisticskingbackendspring.app.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpCommand
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

@Schema(description = "인증 요청")
sealed interface AuthRequest {
    data class SignUpVendor(
        @field:Schema(description = "로그인 ID", example = "vendor01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,
        @field:Schema(description = "이메일", example = "vendor01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,
        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,
        @field:Schema(description = "비밀번호 확인", example = "password1234")
        @field:NotBlank(message = "passwordConfirm은 필수입니다.")
        val passwordConfirm: String?,
        @field:Schema(description = "이름", example = "서울 옷가게")
        @field:NotBlank(message = "name은 필수입니다.")
        val name: String?,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId.orEmpty(),
                email = email.orEmpty(),
                password = password.orEmpty(),
                passwordConfirm = passwordConfirm.orEmpty(),
                name = name.orEmpty(),
                role = UserRole.VENDOR,
            )
        }
    }

    data class SignUpAgency(
        @field:Schema(description = "로그인 ID", example = "agency01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,
        @field:Schema(description = "이메일", example = "agency01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,
        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,
        @field:Schema(description = "비밀번호 확인", example = "password1234")
        @field:NotBlank(message = "passwordConfirm은 필수입니다.")
        val passwordConfirm: String?,
        @field:Schema(description = "이름", example = "CJ 서울 대리점")
        @field:NotBlank(message = "name은 필수입니다.")
        val name: String?,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId.orEmpty(),
                email = email.orEmpty(),
                password = password.orEmpty(),
                passwordConfirm = passwordConfirm.orEmpty(),
                name = name.orEmpty(),
                role = UserRole.AGENCY,
            )
        }
    }

    data class SignUpDriver(
        @field:Schema(description = "로그인 ID", example = "driver01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,
        @field:Schema(description = "이메일", example = "driver01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,
        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,
        @field:Schema(description = "비밀번호 확인", example = "password1234")
        @field:NotBlank(message = "passwordConfirm은 필수입니다.")
        val passwordConfirm: String?,
        @field:Schema(description = "이름", example = "김택배")
        @field:NotBlank(message = "name은 필수입니다.")
        val name: String?,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId.orEmpty(),
                email = email.orEmpty(),
                password = password.orEmpty(),
                passwordConfirm = passwordConfirm.orEmpty(),
                name = name.orEmpty(),
                role = UserRole.DRIVER,
            )
        }
    }

    @Schema(description = "로그인 요청")
    data class Login(
        @field:Schema(description = "로그인 ID", example = "vendor01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,
        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,
    ) : AuthRequest {
        fun toCommand(): LoginCommand {
            return LoginCommand(
                loginId = loginId.orEmpty(),
                password = password.orEmpty(),
            )
        }
    }
}
