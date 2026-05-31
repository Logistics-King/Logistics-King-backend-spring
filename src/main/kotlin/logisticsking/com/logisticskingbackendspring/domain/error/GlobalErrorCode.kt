package logisticsking.com.logisticskingbackendspring.domain.error

import org.springframework.http.HttpStatus

enum class GlobalErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    INVALID_REQUEST(
        code = "INVALID_REQUEST",
        message = "요청 값이 올바르지 않습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INTERNAL_SERVER_ERROR(
        code = "INTERNAL_SERVER_ERROR",
        message = "서버 오류가 발생했습니다.",
        status = HttpStatus.INTERNAL_SERVER_ERROR,
    ),
}
