package logisticsking.com.logisticskingbackendspring.domain.deliver

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class DeliverErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "DELIVER_USER_NOT_FOUND",
        message = "배송기사 사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_DRIVER(
        code = "USER_IS_NOT_DRIVER",
        message = "배송기사 권한 사용자만 사용할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    AGENCY_NOT_FOUND(
        code = "DELIVER_AGENCY_NOT_FOUND",
        message = "소속 대리점을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    DELIVER_ALREADY_EXISTS(
        code = "DELIVER_ALREADY_EXISTS",
        message = "이미 배송기사 프로필이 존재합니다.",
        status = HttpStatus.CONFLICT,
    ),
    DELIVER_NOT_FOUND(
        code = "DELIVER_NOT_FOUND",
        message = "배송기사 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    INVALID_DRIVER_NAME(
        code = "INVALID_DELIVER_DRIVER_NAME",
        message = "기사명은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PHONE_NUMBER(
        code = "INVALID_DELIVER_PHONE_NUMBER",
        message = "연락처는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_SERVICE_REGIONS(
        code = "INVALID_DELIVER_SERVICE_REGIONS",
        message = "담당 가능 지역은 1개 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
