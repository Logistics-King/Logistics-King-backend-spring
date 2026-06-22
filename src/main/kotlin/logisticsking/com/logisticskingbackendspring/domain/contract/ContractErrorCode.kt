package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ContractErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "CONTRACT_USER_NOT_FOUND",
        message = "계약 사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_VENDOR(
        code = "CONTRACT_USER_IS_NOT_VENDOR",
        message = "화주 권한 사용자만 계약을 확정하거나 조회할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    USER_IS_NOT_AGENCY(
        code = "CONTRACT_USER_IS_NOT_AGENCY",
        message = "대리점 권한 사용자만 대리점 계약 목록을 조회할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    VENDOR_NOT_FOUND(
        code = "CONTRACT_VENDOR_NOT_FOUND",
        message = "화주 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    AGENCY_NOT_FOUND(
        code = "CONTRACT_AGENCY_NOT_FOUND",
        message = "대리점 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    PROPOSAL_NOT_FOUND(
        code = "CONTRACT_PROPOSAL_NOT_FOUND",
        message = "계약할 제안을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    CONTRACT_REQUEST_NOT_FOUND(
        code = "CONTRACT_REQUEST_NOT_FOUND",
        message = "계약 요청을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    CONTRACT_ALREADY_EXISTS(
        code = "CONTRACT_ALREADY_EXISTS",
        message = "이미 해당 계약 요청에 확정된 계약이 있습니다.",
        status = HttpStatus.CONFLICT,
    ),
    INVALID_CONTRACT_REQUEST_PROPOSAL(
        code = "INVALID_CONTRACT_REQUEST_PROPOSAL",
        message = "계약 요청과 제안 정보가 일치하지 않습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_CONTRACT_ITEM_UNIT_PRICE(
        code = "INVALID_CONTRACT_ITEM_UNIT_PRICE",
        message = "계약 품목 단가는 1 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
