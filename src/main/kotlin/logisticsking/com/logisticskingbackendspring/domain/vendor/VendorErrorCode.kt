package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class VendorErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "VENDOR_USER_NOT_FOUND",
        message = "화주 사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_VENDOR(
        code = "USER_IS_NOT_VENDOR",
        message = "화주 권한 사용자만 사용할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    USER_IS_NOT_AGENCY(
        code = "USER_IS_NOT_AGENCY",
        message = "대리점 권한 사용자만 사용할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    AGENCY_PRODUCT_PUBLIC_READ_DISABLED(
        code = "AGENCY_PRODUCT_PUBLIC_READ_DISABLED",
        message = "대리점의 화주 배송 품목 전체 조회가 비활성화되어 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    VENDOR_ALREADY_EXISTS(
        code = "VENDOR_ALREADY_EXISTS",
        message = "이미 화주 프로필이 존재합니다.",
        status = HttpStatus.CONFLICT,
    ),
    VENDOR_NOT_FOUND(
        code = "VENDOR_NOT_FOUND",
        message = "화주 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    AGENCY_NOT_FOUND(
        code = "VENDOR_AGENCY_NOT_FOUND",
        message = "대리점 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    PRODUCT_NOT_FOUND(
        code = "VENDOR_PRODUCT_NOT_FOUND",
        message = "화주 배송 품목을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    INVALID_BUSINESS_NAME(
        code = "INVALID_VENDOR_BUSINESS_NAME",
        message = "상호명은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_REPRESENTATIVE_NAME(
        code = "INVALID_VENDOR_REPRESENTATIVE_NAME",
        message = "대표자명은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PHONE_NUMBER(
        code = "INVALID_VENDOR_PHONE_NUMBER",
        message = "연락처는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_ADDRESS(
        code = "INVALID_VENDOR_ADDRESS",
        message = "사업장 주소는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_MAIN_REGION(
        code = "INVALID_VENDOR_MAIN_REGION",
        message = "주 발송 지역은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PRODUCT_NAME(
        code = "INVALID_VENDOR_PRODUCT_NAME",
        message = "배송 품목명은 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_DESTINATION_ADDRESS(
        code = "INVALID_VENDOR_PRODUCT_DESTINATION_ADDRESS",
        message = "배송 목적지 주소는 필수입니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_AVERAGE_PRICE(
        code = "INVALID_VENDOR_PRODUCT_AVERAGE_PRICE",
        message = "평균 상품 가격은 0 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_AVERAGE_WEIGHT(
        code = "INVALID_VENDOR_PRODUCT_AVERAGE_WEIGHT",
        message = "평균 무게는 0 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
    INVALID_PRODUCT_QUANTITY(
        code = "INVALID_VENDOR_PRODUCT_QUANTITY",
        message = "박스 수량과 낱개 수량은 0 이상이어야 하며, 둘 중 하나는 1 이상이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
