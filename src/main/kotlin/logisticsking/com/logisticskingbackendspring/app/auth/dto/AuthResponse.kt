package logisticsking.com.logisticskingbackendspring.app.auth.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "인증 응답")
sealed interface AuthResponse {
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
}
