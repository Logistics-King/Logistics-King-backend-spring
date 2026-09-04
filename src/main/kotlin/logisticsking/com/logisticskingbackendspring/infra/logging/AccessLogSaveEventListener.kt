package logisticsking.com.logisticskingbackendspring.infra.logging

import logisticsking.com.logisticskingbackendspring.domain.accesslog.AccessLogService
import logisticsking.com.logisticskingbackendspring.domain.accesslog.SaveAccessLogCommand
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AccessLogSaveEventListener(
    private val accessLogService: AccessLogService,
) {

    @Async("accessLogTaskExecutor")
    @EventListener
    fun handle(event: AccessLogSaveEvent) {
        try {
            accessLogService.save(
                SaveAccessLogCommand(
                    requestId = event.requestId,
                    userId = event.userId,
                    userRole = event.userRole,
                    method = event.method,
                    path = event.path,
                    queryString = event.queryString,
                    statusCode = event.statusCode,
                    latencyMs = event.latencyMs,
                    errorCode = event.errorCode,
                    clientIp = event.clientIp,
                    userAgent = event.userAgent,
                    occurredAt = event.occurredAt,
                ),
            )
        } catch (exception: Exception) {
            logger.warn("Access log save failed requestId={}", event.requestId, exception)
        }
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(AccessLogSaveEventListener::class.java)
    }
}
