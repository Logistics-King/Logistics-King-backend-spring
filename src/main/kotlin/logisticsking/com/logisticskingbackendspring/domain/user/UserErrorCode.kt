package logisticsking.com.logisticskingbackendspring.domain.user

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    DUPLICATED_LOGIN_ID(
        code = "DUPLICATED_LOGIN_ID",
        message = "이미 사용 중인 로그인 ID입니다.",
        status = HttpStatus.CONFLICT,
    ),
    DUPLICATED_EMAIL(
        code = "DUPLICATED_EMAIL",
        message = "이미 사용 중인 이메일입니다.",
        status = HttpStatus.CONFLICT,
    ),
    INVALID_LOGIN_ID(
        code = "INVALID_LOGIN_ID",
        message = "로그인 ID는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_EMAIL(
        code = "INVALID_EMAIL",
        message = "이메일은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PASSWORD(
        code = "INVALID_PASSWORD",
        message = "비밀번호는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_NAME(
        code = "INVALID_NAME",
        message = "이름은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
