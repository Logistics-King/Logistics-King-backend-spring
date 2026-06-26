package logisticsking.com.logisticskingbackendspring.infra.notification

import logisticsking.com.logisticskingbackendspring.domain.notification.Notification
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationReferenceType
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationRepository
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.time.LocalDateTime
import java.util.UUID

class NotificationSseStreamServiceTest {

    @Test
    fun `Last-Event-ID 이후 알림을 SSE 초기 버퍼에 재전송한다`() {
        val userId = UUID.fromString("019b1f44-a741-7000-8000-000000000001")
        val lastNotification = notification(
            id = UUID.fromString("019b1f44-a741-7000-8000-000000000002"),
            receiverUserId = userId,
            createdAt = LocalDateTime.parse("2026-06-26T10:00:00"),
        )
        val missedNotification = notification(
            id = UUID.fromString("019b1f44-a741-7000-8000-000000000003"),
            receiverUserId = userId,
            createdAt = LocalDateTime.parse("2026-06-26T10:00:01"),
        )
        val repository = FakeNotificationRepository(
            lastNotification = lastNotification,
            missedNotifications = listOf(missedNotification),
        )
        val service = NotificationSseStreamService(repository)

        val emitter = service.subscribe(
            userId = userId,
            lastEventId = lastNotification.id,
        )

        val sentText = emitterEarlySendText(emitter)

        assertTrue(repository.findAfterCalled)
        assertTrue(sentText.contains("event:connected"))
        assertTrue(sentText.contains("event:notification"))
        assertTrue(sentText.contains("id:${missedNotification.id}"))
    }

    @Test
    fun `Last-Event-ID가 본인 알림이 아니면 replay 하지 않는다`() {
        val userId = UUID.fromString("019b1f44-a741-7000-8000-000000000001")
        val unknownLastEventId = UUID.fromString("019b1f44-a741-7000-8000-000000000099")
        val missedNotification = notification(
            id = UUID.fromString("019b1f44-a741-7000-8000-000000000003"),
            receiverUserId = userId,
            createdAt = LocalDateTime.parse("2026-06-26T10:00:01"),
        )
        val repository = FakeNotificationRepository(
            lastNotification = null,
            missedNotifications = listOf(missedNotification),
        )
        val service = NotificationSseStreamService(repository)

        val emitter = service.subscribe(
            userId = userId,
            lastEventId = unknownLastEventId,
        )

        val sentText = emitterEarlySendText(emitter)

        assertFalse(repository.findAfterCalled)
        assertTrue(sentText.contains("event:connected"))
        assertFalse(sentText.contains("event:notification"))
        assertFalse(sentText.contains("id:${missedNotification.id}"))
    }

    private fun notification(
        id: UUID,
        receiverUserId: UUID,
        createdAt: LocalDateTime,
    ): Notification {
        return Notification.restore(
            id = id,
            receiverUserId = receiverUserId,
            senderUserId = null,
            type = NotificationType.PROPOSAL_SUBMITTED,
            title = "새 제안 도착",
            message = "계약 요청에 새 대리점 제안이 도착했습니다.",
            linkUrl = "/proposals/$id",
            referenceType = NotificationReferenceType.PROPOSAL,
            referenceId = id,
            readAt = null,
            createdAt = createdAt,
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun emitterEarlySendText(emitter: ResponseBodyEmitter): String {
        val field = ResponseBodyEmitter::class.java.getDeclaredField("earlySendAttempts")
        field.isAccessible = true

        val attempts = field.get(emitter) as Set<Any>

        return attempts.joinToString(separator = "\n") { attempt ->
            val getData = attempt.javaClass.getMethod("getData")
            getData.invoke(attempt).toString()
        }
    }

    private class FakeNotificationRepository(
        private val lastNotification: Notification?,
        private val missedNotifications: List<Notification>,
    ) : NotificationRepository {

        var findAfterCalled = false

        override fun save(notification: Notification): Notification {
            return notification
        }

        override fun saveAll(notifications: List<Notification>): List<Notification> {
            return notifications
        }

        override fun findRecentByReceiverUserId(
            receiverUserId: UUID,
            from: LocalDateTime,
            pageable: Pageable,
        ): Page<Notification> {
            throw UnsupportedOperationException()
        }

        override fun countUnreadByReceiverUserId(
            receiverUserId: UUID,
            from: LocalDateTime,
        ): Long {
            throw UnsupportedOperationException()
        }

        override fun findByIdAndReceiverUserId(
            id: UUID,
            receiverUserId: UUID,
        ): Notification? {
            return lastNotification
                ?.takeIf { it.id == id && it.receiverUserId == receiverUserId }
        }

        override fun findAfter(
            receiverUserId: UUID,
            lastNotification: Notification,
            limit: Int,
        ): List<Notification> {
            findAfterCalled = true

            return missedNotifications
                .filter { it.receiverUserId == receiverUserId }
                .take(limit)
        }

        override fun markAsRead(
            id: UUID,
            receiverUserId: UUID,
            readAt: LocalDateTime,
        ): Notification? {
            throw UnsupportedOperationException()
        }

        override fun markAllAsRead(
            receiverUserId: UUID,
            readAt: LocalDateTime,
        ): Int {
            throw UnsupportedOperationException()
        }
    }
}
