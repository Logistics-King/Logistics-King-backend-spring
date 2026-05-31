package logisticsking.com.logisticskingbackendspring.app.error

import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalErrorCode
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException::class)
    fun handleGlobalException(exception: GlobalException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = exception.errorCode

        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.error(
                    code = errorCode.code,
                    errorMessage = errorCode.message,
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR

        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.error(
                    code = errorCode.code,
                    errorMessage = errorCode.message,
                )
            )
    }
}
