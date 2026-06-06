package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ContractRequestErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "CONTRACT_REQUEST_USER_NOT_FOUND",
        message = "계약 요청 사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_VENDOR(
        code = "CONTRACT_REQUEST_USER_IS_NOT_VENDOR",
        message = "화주 권한 사용자만 계약 요청을 등록할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    VENDOR_NOT_FOUND(
        code = "CONTRACT_REQUEST_VENDOR_NOT_FOUND",
        message = "화주 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    PRODUCT_NOT_FOUND(
        code = "CONTRACT_REQUEST_PRODUCT_NOT_FOUND",
        message = "화주 배송 품목을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    NOT_FOUND(
        code = "CONTRACT_REQUEST_NOT_FOUND",
        message = "계약 요청을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    INVALID_PICKUP_REGION(
        code = "INVALID_CONTRACT_REQUEST_PICKUP_REGION",
        message = "픽업 지역은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_MONTHLY_VOLUME(
        code = "INVALID_CONTRACT_REQUEST_MONTHLY_VOLUME",
        message = "월 예상 물량은 1 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PRODUCT_NAME(
        code = "INVALID_CONTRACT_REQUEST_PRODUCT_NAME",
        message = "품목명은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_BOX_SIZE(
        code = "INVALID_CONTRACT_REQUEST_BOX_SIZE",
        message = "박스 크기는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PICKUP_TIME(
        code = "INVALID_CONTRACT_REQUEST_PICKUP_TIME",
        message = "픽업 희망 시간은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_TARGET_UNIT_PRICE(
        code = "INVALID_CONTRACT_REQUEST_TARGET_UNIT_PRICE",
        message = "희망 단가는 0 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    CANCELED_REQUEST_CANNOT_BE_UPDATED(
        code = "CANCELED_CONTRACT_REQUEST_CANNOT_BE_UPDATED",
        message = "취소된 계약 요청은 수정할 수 없습니다.",
        status = HttpStatus.CONFLICT,
    ),
}
