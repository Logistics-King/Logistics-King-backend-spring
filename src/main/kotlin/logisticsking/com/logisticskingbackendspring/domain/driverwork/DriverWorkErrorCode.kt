package logisticsking.com.logisticskingbackendspring.domain.driverwork

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class DriverWorkErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "DRIVER_WORK_USER_NOT_FOUND",
        message = "사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_AGENCY(
        code = "DRIVER_WORK_USER_IS_NOT_AGENCY",
        message = "대리점 사용자만 기사 일감을 관리할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    USER_IS_NOT_DRIVER(
        code = "DRIVER_WORK_USER_IS_NOT_DRIVER",
        message = "배송기사 사용자만 기사 일감에 신청할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    AGENCY_NOT_FOUND(
        code = "DRIVER_WORK_AGENCY_NOT_FOUND",
        message = "대리점을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    CONTRACT_NOT_FOUND(
        code = "DRIVER_WORK_CONTRACT_NOT_FOUND",
        message = "계약을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    CONTRACT_DOES_NOT_BELONG_TO_AGENCY(
        code = "DRIVER_WORK_CONTRACT_DOES_NOT_BELONG_TO_AGENCY",
        message = "대리점의 계약에 대해서만 기사 일감을 만들 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    DELIVER_NOT_FOUND(
        code = "DRIVER_WORK_DELIVER_NOT_FOUND",
        message = "배송기사를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    DELIVER_IS_NOT_AGENCY_AFFILIATED(
        code = "DRIVER_WORK_DELIVER_IS_NOT_AGENCY_AFFILIATED",
        message = "대리점 소속 배송기사만 소속 기사 일감을 받을 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    DELIVER_DOES_NOT_BELONG_TO_AGENCY(
        code = "DRIVER_WORK_DELIVER_DOES_NOT_BELONG_TO_AGENCY",
        message = "해당 대리점 소속 배송기사가 아닙니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    NOT_FOUND(
        code = "DRIVER_WORK_NOT_FOUND",
        message = "기사 일감을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    APPLICATION_NOT_FOUND(
        code = "DRIVER_WORK_APPLICATION_NOT_FOUND",
        message = "기사 일감 신청을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    ALREADY_APPLIED(
        code = "DRIVER_WORK_ALREADY_APPLIED",
        message = "이미 신청한 기사 일감입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_TITLE(
        code = "INVALID_DRIVER_WORK_TITLE",
        message = "기사 일감 제목은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_SERVICE_REGION(
        code = "INVALID_DRIVER_WORK_SERVICE_REGION",
        message = "기사 일감 담당 지역은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_EXPECTED_VOLUME(
        code = "INVALID_DRIVER_WORK_EXPECTED_VOLUME",
        message = "기사 일감 예상 물량은 1 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_UNIT_PRICE(
        code = "INVALID_DRIVER_WORK_UNIT_PRICE",
        message = "기사 일감 정산 단가는 0보다 커야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_DATE_RANGE(
        code = "INVALID_DRIVER_WORK_DATE_RANGE",
        message = "기사 일감 종료일은 시작일보다 빠를 수 없습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_OPEN_WORK_CAN_BE_APPLIED(
        code = "ONLY_OPEN_DRIVER_WORK_CAN_BE_APPLIED",
        message = "모집 중인 기사 일감에만 신청할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_OPEN_WORK_CAN_BE_ASSIGNED(
        code = "ONLY_OPEN_DRIVER_WORK_CAN_BE_ASSIGNED",
        message = "모집 중인 기사 일감만 담당 기사를 확정할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_ASSIGNED_WORK_CAN_BE_COMPLETED(
        code = "ONLY_ASSIGNED_DRIVER_WORK_CAN_BE_COMPLETED",
        message = "담당 기사가 확정된 기사 일감만 완료할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    COMPLETED_WORK_CANNOT_BE_CANCELLED(
        code = "COMPLETED_DRIVER_WORK_CANNOT_BE_CANCELLED",
        message = "완료된 기사 일감은 취소할 수 없습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    ONLY_APPLIED_APPLICATION_CAN_BE_CHANGED(
        code = "ONLY_APPLIED_DRIVER_WORK_APPLICATION_CAN_BE_CHANGED",
        message = "신청 상태의 기사 일감 신청만 변경할 수 있습니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
