package logisticsking.com.logisticskingbackendspring.domain.accesslog

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class AccessLogService(
    private val accessLogRepository: AccessLogRepository,
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun save(command: SaveAccessLogCommand): AccessLog {
        return accessLogRepository.save(
            AccessLog.create(
                requestId = command.requestId,
                userId = command.userId,
                userRole = command.userRole,
                method = command.method,
                path = command.path,
                queryString = command.queryString,
                statusCode = command.statusCode,
                latencyMs = command.latencyMs,
                errorCode = command.errorCode,
                clientIp = command.clientIp,
                userAgent = command.userAgent,
                occurredAt = command.occurredAt,
            ),
        )
    }
}
