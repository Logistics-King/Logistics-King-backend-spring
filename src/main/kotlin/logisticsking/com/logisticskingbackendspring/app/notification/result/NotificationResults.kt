package logisticsking.com.logisticskingbackendspring.app.notification.result

import logisticsking.com.logisticskingbackendspring.domain.notification.Notification
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationReferenceType
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationType
import java.time.LocalDateTime
import java.util.UUID

data class NotificationResult(
    val notificationId: UUID,
    val receiverUserId: UUID,
    val senderUserId: UUID?,
    val type: NotificationType,
    val title: String,
    val message: String,
    val linkUrl: String?,
    val referenceType: NotificationReferenceType?,
    val referenceId: UUID?,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun from(notification: Notification): NotificationResult {
            return NotificationResult(
                notificationId = notification.id,
                receiverUserId = notification.receiverUserId,
                senderUserId = notification.senderUserId,
                type = notification.type,
                title = notification.title,
                message = notification.message,
                linkUrl = notification.linkUrl,
                referenceType = notification.referenceType,
                referenceId = notification.referenceId,
                readAt = notification.readAt,
                createdAt = notification.createdAt,
            )
        }
    }
}

data class UnreadNotificationCountResult(
    val count: Long,
)

data class ReadAllNotificationsResult(
    val readCount: Int,
)
