package logisticsking.com.logisticskingbackendspring.domain.recommendation

import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.HttpStatus

enum class RecommendationErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "RECOMMENDATION_USER_NOT_FOUND",
        message = "추천 요청 사용자를 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    USER_IS_NOT_VENDOR(
        code = "RECOMMENDATION_USER_IS_NOT_VENDOR",
        message = "화주 사용자만 대리점 추천을 조회할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    USER_IS_NOT_AGENCY(
        code = "RECOMMENDATION_USER_IS_NOT_AGENCY",
        message = "대리점 사용자만 화주 추천을 조회할 수 있습니다.",
        status = HttpStatus.FORBIDDEN,
    ),
    VENDOR_NOT_FOUND(
        code = "RECOMMENDATION_VENDOR_NOT_FOUND",
        message = "화주 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    AGENCY_NOT_FOUND(
        code = "RECOMMENDATION_AGENCY_NOT_FOUND",
        message = "대리점 프로필을 찾을 수 없습니다.",
        status = HttpStatus.NOT_FOUND,
    ),
    INVALID_LIMIT(
        code = "INVALID_RECOMMENDATION_LIMIT",
        message = "추천 조회 개수는 1 이상 50 이하이어야 합니다.",
        status = HttpStatus.BAD_REQUEST,
    ),
}
