package logisticsking.com.logisticskingbackendspring.infra.persistence.accesslog

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.accesslog.AccessLog
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "access_logs")
class AccessLogJpaEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Column(name = "request_id", nullable = false)
    val requestId: Long,

    @Column(name = "user_id", nullable = true, columnDefinition = "BINARY(16)")
    val userId: UUID?,

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = true, length = 30)
    val userRole: UserRole?,

    @Column(name = "method", nullable = false, length = 10)
    val method: String,

    @Column(name = "path", nullable = false, length = 1000)
    val path: String,

    @Column(name = "query_string", nullable = true, length = 2000)
    val queryString: String?,

    @Column(name = "status_code", nullable = false)
    val statusCode: Int,

    @Column(name = "latency_ms", nullable = false)
    val latencyMs: Long,

    @Column(name = "error_code", nullable = true, length = 100)
    val errorCode: String?,

    @Column(name = "client_ip", nullable = true, length = 100)
    val clientIp: String?,

    @Column(name = "user_agent", nullable = true, length = 1000)
    val userAgent: String?,

    @Column(name = "occurred_at", nullable = false)
    val occurredAt: LocalDateTime,
) : BaseJpaEntity() {

    fun toDomain(): AccessLog {
        return AccessLog.restore(
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

    companion object {
        fun from(accessLog: AccessLog): AccessLogJpaEntity {
            return AccessLogJpaEntity(
                id = accessLog.id ?: 0,
                requestId = accessLog.requestId,
                userId = accessLog.userId,
                userRole = accessLog.userRole,
                method = accessLog.method,
                path = accessLog.path,
                queryString = accessLog.queryString,
                statusCode = accessLog.statusCode,
                latencyMs = accessLog.latencyMs,
                errorCode = accessLog.errorCode,
                clientIp = accessLog.clientIp,
                userAgent = accessLog.userAgent,
                occurredAt = accessLog.occurredAt,
            )
        }
    }
}
