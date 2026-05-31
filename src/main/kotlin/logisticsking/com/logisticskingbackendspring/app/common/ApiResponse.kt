package logisticsking.com.logisticskingbackendspring.app.common

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "공통 API 응답")
data class ApiResponse<T>(
    @field:Schema(description = "응답 payload")
    val payload: ApiPayload<T>,
) {
    companion object {
        fun <T> success(
            code: String = "SUCCESS",
            response: T,
        ): ApiResponse<T> {
            return ApiResponse(
                payload = ApiPayload(
                    code = code,
                    errorMessage = null,
                    response = response,
                )
            )
        }

        fun error(
            code: String,
            errorMessage: String,
        ): ApiResponse<Nothing> {
            return ApiResponse(
                payload = ApiPayload(
                    code = code,
                    errorMessage = errorMessage,
                    response = null,
                )
            )
        }
    }
}

@Schema(description = "공통 API payload")
data class ApiPayload<T>(
    @field:Schema(description = "응답 코드", example = "SUCCESS")
    val code: String,
    @field:Schema(description = "에러 메시지. 성공이면 null", example = "인증에 실패했습니다.")
    val errorMessage: String?,
    @field:Schema(description = "실제 응답 객체. 실패이면 null")
    val response: T?,
)
