package logisticsking.com.logisticskingbackendspring.app.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "인증 응답")
sealed interface AuthResponse {
    @Schema(description = "회원가입 응답")
    data class SignUp(
        @field:Schema(description = "사용자 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val userId: String,

        @field:Schema(description = "권한", example = "VENDOR")
        val role: String,
    ) : AuthResponse

    @Schema(description = "로그인 응답")
    data class Login(
        @field:Schema(description = "사용자 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val userId: String,

        @field:Schema(description = "권한", example = "VENDOR")
        val role: String,
    ) : AuthResponse

    @Schema(description = "토큰 재발급 응답")
    data class Refresh(
        @field:Schema(description = "사용자 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val userId: String,

        @field:Schema(description = "권한", example = "VENDOR")
        val role: String,
    ) : AuthResponse

    @Schema(description = "로그아웃 응답")
    data class Logout(
        @field:Schema(description = "로그아웃 여부", example = "true")
        val loggedOut: Boolean,
    ) : AuthResponse

    @Schema(description = "계정 복구 요청 응답")
    data class AccountRecoveryRequest(
        @field:Schema(description = "요청 접수 여부", example = "true")
        val accepted: Boolean,

        @field:Schema(description = "사용자 안내 문구", example = "입력한 정보가 일치하면 이메일을 발송합니다.")
        val message: String,
    ) : AuthResponse

    @Schema(description = "비밀번호 재설정 응답")
    data class ResetPassword(
        @field:Schema(description = "비밀번호 재설정 여부", example = "true")
        val reset: Boolean,
    ) : AuthResponse
}
