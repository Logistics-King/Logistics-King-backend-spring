package logisticsking.com.logisticskingbackendspring.infra.security

import jakarta.servlet.http.HttpServletResponse
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.domain.error.ErrorCode
import org.slf4j.LoggerFactory
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
        logger.warn(
            "Security request rejected status={} errorCode={}",
            errorCode.status.value(),
            errorCode.code,
        )
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

    private companion object {
        private val logger = LoggerFactory.getLogger(SecurityResponseWriter::class.java)
    }
}
