package logisticsking.com.logisticskingbackendspring.domain.accesslog

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.time.LocalDateTime
import java.util.UUID

class AccessLog private constructor(
    val id: Long?,

    val requestId: Long,

    val userId: UUID?,

    val userRole: UserRole?,

    val method: String,

    val path: String,

    val queryString: String?,

    val statusCode: Int,

    val latencyMs: Long,

    val errorCode: String?,

    val clientIp: String?,

    val userAgent: String?,

    val occurredAt: LocalDateTime,
) {

    companion object {
        fun create(
            requestId: Long,
            userId: UUID?,
            userRole: UserRole?,
            method: String,
            path: String,
            queryString: String?,
            statusCode: Int,
            latencyMs: Long,
            errorCode: String?,
            clientIp: String?,
            userAgent: String?,
            occurredAt: LocalDateTime,
        ): AccessLog {
            return AccessLog(
                id = null,
                requestId = requestId,
                userId = userId,
                userRole = userRole,
                method = method.take(MAX_METHOD_LENGTH),
                path = path.take(MAX_PATH_LENGTH),
                queryString = queryString?.take(MAX_QUERY_STRING_LENGTH),
                statusCode = statusCode,
                latencyMs = latencyMs,
                errorCode = errorCode?.take(MAX_ERROR_CODE_LENGTH),
                clientIp = clientIp?.take(MAX_CLIENT_IP_LENGTH),
                userAgent = userAgent?.take(MAX_USER_AGENT_LENGTH),
                occurredAt = occurredAt,
            )
        }

        fun restore(
            id: Long,
            requestId: Long,
            userId: UUID?,
            userRole: UserRole?,
            method: String,
            path: String,
            queryString: String?,
            statusCode: Int,
            latencyMs: Long,
            errorCode: String?,
            clientIp: String?,
            userAgent: String?,
            occurredAt: LocalDateTime,
        ): AccessLog {
            return AccessLog(
                id = id,
                requestId = requestId,
                userId = userId,
                userRole = userRole,
                method = method,
                path = path,
                queryString = queryString,
                statusCode = statusCode,
                latencyMs = latencyMs,
                errorCode = errorCode,
                clientIp = clientIp,
                userAgent = userAgent,
                occurredAt = occurredAt,
            )
        }

        private const val MAX_METHOD_LENGTH = 10
        private const val MAX_PATH_LENGTH = 1_000
        private const val MAX_QUERY_STRING_LENGTH = 2_000
        private const val MAX_ERROR_CODE_LENGTH = 100
        private const val MAX_CLIENT_IP_LENGTH = 100
        private const val MAX_USER_AGENT_LENGTH = 1_000
    }
}
