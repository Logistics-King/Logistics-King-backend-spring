package logisticsking.com.logisticskingbackendspring.domain.notification

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class NotificationErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    NOT_FOUND("NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_TITLE("NOTIFICATION_INVALID_TITLE", "알림 제목은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_MESSAGE("NOTIFICATION_INVALID_MESSAGE", "알림 메시지는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST),
}
