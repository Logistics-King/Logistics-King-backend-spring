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
    INVALID_PROPOSAL_ITEMS(
        code = "INVALID_PROPOSAL_ITEMS",
        message = "계약 요청 배송 품목별 제안 단가가 필요합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PROPOSAL_ITEM_MATCH(
        code = "INVALID_PROPOSAL_ITEM_MATCH",
        message = "계약 요청 배송 품목과 제안 품목 단가가 일치하지 않습니다.",
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
    PROPOSAL_CANNOT_BE_NEGOTIATED(
        code = "PROPOSAL_CANNOT_BE_NEGOTIATED",
        message = "협상 가능한 상태의 제안이 아닙니다.",
        status = HttpStatus.CONFLICT,
    ),
    PROPOSAL_HAS_PENDING_NEGOTIATION(
        code = "PROPOSAL_HAS_PENDING_NEGOTIATION",
        message = "응답 대기 중인 협상 제안이 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    PROPOSAL_CANNOT_ACCEPT_NEGOTIATION(
        code = "PROPOSAL_CANNOT_ACCEPT_NEGOTIATION",
        message = "수락 가능한 협상 상태가 아닙니다.",
        status = HttpStatus.CONFLICT,
    ),
    PROPOSAL_CANNOT_REJECT_NEGOTIATION(
        code = "PROPOSAL_CANNOT_REJECT_NEGOTIATION",
        message = "거절 가능한 협상 상태가 아닙니다.",
        status = HttpStatus.CONFLICT,
    ),
    NEGOTIATION_EVENT_NOT_FOUND(
        code = "NEGOTIATION_EVENT_NOT_FOUND",
        message = "협상 이벤트를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    NEGOTIATION_EVENT_IS_NOT_PENDING(
        code = "NEGOTIATION_EVENT_IS_NOT_PENDING",
        message = "응답 대기 중인 협상 제안이 아닙니다.",
        status = HttpStatus.CONFLICT,
    ),
    INVALID_NEGOTIATION_ACTOR(
        code = "INVALID_NEGOTIATION_ACTOR",
        message = "협상에 참여할 수 없는 사용자입니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    INVALID_NEGOTIATION_RESPONDER(
        code = "INVALID_NEGOTIATION_RESPONDER",
        message = "자신이 보낸 협상 제안에는 응답할 수 없습니다.",
        status = HttpStatus.CONFLICT,
    ),
    INVALID_NEGOTIATION_SEQUENCE(
        code = "INVALID_NEGOTIATION_SEQUENCE",
        message = "협상 순번은 1 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_NEGOTIATION_EVENT_TYPE(
        code = "INVALID_NEGOTIATION_EVENT_TYPE",
        message = "협상 이벤트 타입이 올바르지 않습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
