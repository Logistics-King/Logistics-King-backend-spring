package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ProposalErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "PROPOSAL_USER_NOT_FOUND",
        message = "제안 사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_AGENCY(
        code = "PROPOSAL_USER_IS_NOT_AGENCY",
        message = "대리점 권한 사용자만 제안을 등록할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    USER_IS_NOT_VENDOR(
        code = "PROPOSAL_USER_IS_NOT_VENDOR",
        message = "화주 권한 사용자만 제안을 조회할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    AGENCY_NOT_FOUND(
        code = "PROPOSAL_AGENCY_NOT_FOUND",
        message = "대리점 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    VENDOR_NOT_FOUND(
        code = "PROPOSAL_VENDOR_NOT_FOUND",
        message = "화주 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    CONTRACT_REQUEST_NOT_FOUND(
        code = "PROPOSAL_CONTRACT_REQUEST_NOT_FOUND",
        message = "계약 요청을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    CONTRACT_REQUEST_IS_NOT_OPEN(
        code = "PROPOSAL_CONTRACT_REQUEST_IS_NOT_OPEN",
        message = "열려 있는 계약 요청에만 제안할 수 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    ALREADY_EXISTS(
        code = "PROPOSAL_ALREADY_EXISTS",
        message = "이미 해당 계약 요청에 제출한 제안이 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    NOT_FOUND(
        code = "PROPOSAL_NOT_FOUND",
        message = "제안을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    INVALID_UNIT_PRICE(
        code = "INVALID_PROPOSAL_UNIT_PRICE",
        message = "제안 단가는 1 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PICKUP_TIME(
        code = "INVALID_PROPOSAL_PICKUP_TIME",
        message = "제안 픽업 시간은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_SUBMITTED_PROPOSAL_CAN_BE_UPDATED(
        code = "ONLY_SUBMITTED_PROPOSAL_CAN_BE_UPDATED",
        message = "제출 상태의 제안만 수정할 수 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    ONLY_SUBMITTED_PROPOSAL_CAN_BE_WITHDRAWN(
        code = "ONLY_SUBMITTED_PROPOSAL_CAN_BE_WITHDRAWN",
        message = "제출 상태의 제안만 철회할 수 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    ONLY_SUBMITTED_PROPOSAL_CAN_BE_ACCEPTED(
        code = "ONLY_SUBMITTED_PROPOSAL_CAN_BE_ACCEPTED",
        message = "제출 상태의 제안만 수락할 수 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    ONLY_SUBMITTED_PROPOSAL_CAN_BE_REJECTED(
        code = "ONLY_SUBMITTED_PROPOSAL_CAN_BE_REJECTED",
        message = "제출 상태의 제안만 거절할 수 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
}
