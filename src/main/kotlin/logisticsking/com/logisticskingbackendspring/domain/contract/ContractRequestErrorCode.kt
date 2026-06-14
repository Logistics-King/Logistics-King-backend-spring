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
    AGENCY_NOT_FOUND(
        code = "CONTRACT_REQUEST_AGENCY_NOT_FOUND",
        message = "대리점 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    NOT_FOUND(
        code = "CONTRACT_REQUEST_NOT_FOUND",
        message = "계약 요청을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_ROLE_NOT_SUPPORTED(
        code = "CONTRACT_REQUEST_USER_ROLE_NOT_SUPPORTED",
        message = "계약 요청은 화주 또는 대리점 사용자만 등록할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    INVALID_CONTRACT_PARTY(
        code = "CONTRACT_REQUEST_INVALID_PARTY",
        message = "계약 요청의 요청자와 승인자 정보가 올바르지 않습니다.",
        status = HttpStatus.BAD_REQUEST,
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
    TARGET_UNIT_PRICE_REQUIRED(
        code = "CONTRACT_REQUEST_TARGET_UNIT_PRICE_REQUIRED",
        message = "계약 요청을 바로 수락하려면 단가가 필요합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    CANCELED_REQUEST_CANNOT_BE_UPDATED(
        code = "CANCELED_CONTRACT_REQUEST_CANNOT_BE_UPDATED",
        message = "취소된 계약 요청은 수정할 수 없습니다.",
        status = HttpStatus.CONFLICT,
    ),
    CONTRACTED_REQUEST_CANNOT_BE_UPDATED(
        code = "CONTRACTED_CONTRACT_REQUEST_CANNOT_BE_UPDATED",
        message = "계약이 확정된 계약 요청은 수정할 수 없습니다.",
        status = HttpStatus.CONFLICT,
    ),
    CONTRACTED_REQUEST_CANNOT_BE_CANCELED(
        code = "CONTRACTED_CONTRACT_REQUEST_CANNOT_BE_CANCELED",
        message = "계약이 확정된 계약 요청은 취소할 수 없습니다.",
        status = HttpStatus.CONFLICT,
    ),
    REJECTED_REQUEST_CANNOT_BE_UPDATED(
        code = "REJECTED_CONTRACT_REQUEST_CANNOT_BE_UPDATED",
        message = "거절된 계약 요청은 수정할 수 없습니다.",
        status = HttpStatus.CONFLICT,
    ),
    REJECTED_REQUEST_CANNOT_BE_CANCELED(
        code = "REJECTED_CONTRACT_REQUEST_CANNOT_BE_CANCELED",
        message = "거절된 계약 요청은 취소할 수 없습니다.",
        status = HttpStatus.CONFLICT,
    ),
    ONLY_OPEN_REQUEST_CAN_BE_REJECTED(
        code = "ONLY_OPEN_CONTRACT_REQUEST_CAN_BE_REJECTED",
        message = "OPEN 상태의 계약 요청만 거절할 수 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    ONLY_OPEN_REQUEST_CAN_BE_CONTRACTED(
        code = "ONLY_OPEN_CONTRACT_REQUEST_CAN_BE_CONTRACTED",
        message = "OPEN 상태의 계약 요청만 계약 확정할 수 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
}
