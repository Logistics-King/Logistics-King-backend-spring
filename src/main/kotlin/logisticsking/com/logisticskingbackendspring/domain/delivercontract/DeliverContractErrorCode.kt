package logisticsking.com.logisticskingbackendspring.domain.delivercontract

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class DeliverContractErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "DELIVER_CONTRACT_USER_NOT_FOUND",
        message = "사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_AGENCY(
        code = "DELIVER_CONTRACT_USER_IS_NOT_AGENCY",
        message = "대리점 사용자만 배송기사 계약을 생성하거나 관리할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    USER_IS_NOT_DRIVER(
        code = "DELIVER_CONTRACT_USER_IS_NOT_DRIVER",
        message = "배송기사 사용자만 배송기사 계약을 수락하거나 거절할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    AGENCY_NOT_FOUND(
        code = "DELIVER_CONTRACT_AGENCY_NOT_FOUND",
        message = "대리점 정보를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    DELIVER_NOT_FOUND(
        code = "DELIVER_CONTRACT_DELIVER_NOT_FOUND",
        message = "배송기사 정보를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    DELIVER_DOES_NOT_BELONG_TO_AGENCY(
        code = "DELIVER_DOES_NOT_BELONG_TO_AGENCY",
        message = "해당 배송기사는 로그인한 대리점 소속이 아닙니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    NOT_FOUND(
        code = "DELIVER_CONTRACT_NOT_FOUND",
        message = "배송기사 계약을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    ALREADY_EXISTS(
        code = "DELIVER_CONTRACT_ALREADY_EXISTS",
        message = "이미 요청 또는 수락된 배송기사 계약이 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    INVALID_SERVICE_REGION(
        code = "INVALID_DELIVER_CONTRACT_SERVICE_REGION",
        message = "배송기사 계약 담당 지역은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_EXPECTED_MONTHLY_VOLUME(
        code = "INVALID_DELIVER_CONTRACT_EXPECTED_MONTHLY_VOLUME",
        message = "배송기사 계약 예상 월 물량은 1 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_UNIT_PRICE(
        code = "INVALID_DELIVER_CONTRACT_UNIT_PRICE",
        message = "배송기사 계약 건당 단가는 0보다 커야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_DATE_RANGE(
        code = "INVALID_DELIVER_CONTRACT_DATE_RANGE",
        message = "배송기사 계약 종료일은 시작일보다 빠를 수 없습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_REQUESTED_CONTRACT_CAN_BE_UPDATED(
        code = "ONLY_REQUESTED_DELIVER_CONTRACT_CAN_BE_UPDATED",
        message = "요청 상태의 배송기사 계약만 수정할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_REQUESTED_CONTRACT_CAN_BE_ACCEPTED(
        code = "ONLY_REQUESTED_DELIVER_CONTRACT_CAN_BE_ACCEPTED",
        message = "요청 상태의 배송기사 계약만 수락할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_REQUESTED_CONTRACT_CAN_BE_REJECTED(
        code = "ONLY_REQUESTED_DELIVER_CONTRACT_CAN_BE_REJECTED",
        message = "요청 상태의 배송기사 계약만 거절할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_REQUESTED_CONTRACT_CAN_BE_CANCELLED(
        code = "ONLY_REQUESTED_DELIVER_CONTRACT_CAN_BE_CANCELLED",
        message = "요청 상태의 배송기사 계약만 취소할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
