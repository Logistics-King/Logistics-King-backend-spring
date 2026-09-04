package logisticsking.com.logisticskingbackendspring.domain.accesslog

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.time.LocalDateTime
import java.util.UUID

data class SaveAccessLogCommand(
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
)
