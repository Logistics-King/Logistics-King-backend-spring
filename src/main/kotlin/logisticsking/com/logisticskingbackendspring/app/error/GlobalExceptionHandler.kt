package logisticsking.com.logisticskingbackendspring.app.error

import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalErrorCode
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.UUID

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

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(exception: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = GlobalErrorCode.INVALID_REQUEST
        val message = buildRequestBodyErrorMessage(exception)

        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.error(
                    code = errorCode.code,
                    errorMessage = message,
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = GlobalErrorCode.INVALID_REQUEST
        val fieldError = exception.bindingResult.fieldErrors.firstOrNull()
        val message = if (fieldError == null) {
            errorCode.message
        } else {
            "요청 값이 올바르지 않습니다. '${fieldError.field}' 값이 잘못되었습니다. ${fieldError.defaultMessage ?: "요청 값을 확인해 주세요."}"
        }

        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.error(
                    code = errorCode.code,
                    errorMessage = message,
                )
            )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameter(
        exception: MissingServletRequestParameterException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = GlobalErrorCode.INVALID_REQUEST

        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.error(
                    code = errorCode.code,
                    errorMessage = "요청 값이 올바르지 않습니다. '${exception.parameterName}' 파라미터가 필요합니다.",
                )
            )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        exception: MethodArgumentTypeMismatchException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val errorCode = GlobalErrorCode.INVALID_REQUEST
        val expectedType = exception.requiredType?.toExpectedTypeName() ?: "올바른 형식"

        return ResponseEntity
            .status(errorCode.status)
            .body(
                ApiResponse.error(
                    code = errorCode.code,
                    errorMessage = "요청 값이 올바르지 않습니다. '${exception.name}' 값은 $expectedType 형식이어야 합니다.",
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

    private fun buildRequestBodyErrorMessage(exception: HttpMessageNotReadableException): String {
        val detail = exception.mostSpecificCause.message ?: exception.message ?: return GlobalErrorCode.INVALID_REQUEST.message
        val fieldName = extractFieldName(detail)

        if (fieldName != null) {
            return "요청 값이 올바르지 않습니다. '$fieldName' 필드가 누락되었거나 형식이 맞지 않습니다. 요청 body를 확인해 주세요."
        }

        return when {
            detail.contains("Cannot deserialize", ignoreCase = true) ->
                "요청 값이 올바르지 않습니다. 일부 필드 타입이 기대 형식과 다릅니다. 요청 body를 확인해 주세요."
            detail.contains("Unexpected character", ignoreCase = true) ||
                detail.contains("Unexpected end-of-input", ignoreCase = true) ->
                "요청 JSON 형식이 올바르지 않습니다. 문법을 확인해 주세요."
            else -> GlobalErrorCode.INVALID_REQUEST.message
        }
    }

    private fun extractFieldName(message: String): String? {
        val patterns = listOf(
            Regex("""JSON property ['"]([^'"]+)['"]"""),
            Regex("""property ['"]([^'"]+)['"]"""),
            Regex("""field ['"]([^'"]+)['"]"""),
            Regex("""\["([^"]+)"]"""),
        )

        return patterns.firstNotNullOfOrNull { pattern ->
            pattern.find(message)?.groupValues?.getOrNull(1)
        }
    }

    private fun Class<*>.toExpectedTypeName(): String {
        return when (this) {
            String::class.java -> "문자열"
            java.lang.Integer::class.java,
            Int::class.java -> "정수"
            java.lang.Long::class.java,
            Long::class.java -> "정수"
            java.lang.Boolean::class.java,
            Boolean::class.java -> "true 또는 false"
            UUID::class.java -> "UUID"
            else -> simpleName
        }
    }
}
