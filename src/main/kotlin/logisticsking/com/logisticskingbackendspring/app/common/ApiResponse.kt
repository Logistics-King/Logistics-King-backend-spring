package logisticsking.com.logisticskingbackendspring.app.common

data class ApiResponse<T>(
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

data class ApiPayload<T>(
    val code: String,
    val errorMessage: String?,
    val response: T?,
)
