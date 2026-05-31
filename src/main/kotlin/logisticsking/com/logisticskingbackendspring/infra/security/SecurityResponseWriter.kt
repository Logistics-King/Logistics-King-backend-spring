package logisticsking.com.logisticskingbackendspring.infra.security

import jakarta.servlet.http.HttpServletResponse
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class SecurityResponseWriter(
    private val objectMapper: ObjectMapper,
) {

    fun writeError(
        response: HttpServletResponse,
        errorCode: ErrorCode,
    ) {
        response.status = errorCode.status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(
            objectMapper.writeValueAsString(
                ApiResponse.error(
                    code = errorCode.code,
                    errorMessage = errorCode.message,
                )
            )
        )
    }
}
