package logisticsking.com.logisticskingbackendspring.domain.agency

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class AgencyErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "AGENCY_USER_NOT_FOUND",
        message = "대리점 사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_AGENCY(
        code = "USER_IS_NOT_AGENCY",
        message = "대리점 권한 사용자만 사용할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    AGENCY_ALREADY_EXISTS(
        code = "AGENCY_ALREADY_EXISTS",
        message = "이미 대리점 프로필이 존재합니다.",
        status = HttpStatus.CONFLICT,
    ),
    AGENCY_NOT_FOUND(
        code = "AGENCY_NOT_FOUND",
        message = "대리점 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    INVALID_AGENCY_NAME(
        code = "INVALID_AGENCY_NAME",
        message = "대리점명은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_REPRESENTATIVE_NAME(
        code = "INVALID_AGENCY_REPRESENTATIVE_NAME",
        message = "대표자명은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PHONE_NUMBER(
        code = "INVALID_AGENCY_PHONE_NUMBER",
        message = "연락처는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_ADDRESS(
        code = "INVALID_AGENCY_ADDRESS",
        message = "대리점 주소는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_MAIN_REGION(
        code = "INVALID_AGENCY_MAIN_REGION",
        message = "주 담당 지역은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_SERVICE_REGIONS(
        code = "INVALID_AGENCY_SERVICE_REGIONS",
        message = "담당 가능 지역은 1개 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_MAX_MONTHLY_VOLUME(
        code = "INVALID_AGENCY_MAX_MONTHLY_VOLUME",
        message = "월 처리 가능 물량은 0 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
