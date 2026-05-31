package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    INVALID_CREDENTIALS(
        code = "INVALID_CREDENTIALS",
        message = "아이디 또는 비밀번호가 올바르지 않습니다.",
        status = HttpStatus.UNAUTHORIZED,
    ),
    UNAUTHORIZED(
        code = "UNAUTHORIZED",
        message = "인증이 필요합니다.",
        status = HttpStatus.UNAUTHORIZED,
    ),
    FORBIDDEN(
        code = "FORBIDDEN",
        message = "접근 권한이 없습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    INVALID_TOKEN(
        code = "INVALID_TOKEN",
        message = "토큰이 올바르지 않습니다.",
        status = HttpStatus.UNAUTHORIZED,
    ),
    REFRESH_TOKEN_NOT_FOUND(
        code = "REFRESH_TOKEN_NOT_FOUND",
        message = "Refresh Token을 찾을 수 없습니다.",
        status = HttpStatus.UNAUTHORIZED,
    ),
}
