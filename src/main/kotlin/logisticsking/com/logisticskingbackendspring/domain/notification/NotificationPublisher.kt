package logisticsking.com.logisticskingbackendspring.domain.notification

import java.util.UUID

interface NotificationPublisher {
    fun publish(
        receiverUserId: UUID,
        senderUserId: UUID?,
        type: NotificationType,
        referenceType: NotificationReferenceType?,
        referenceId: UUID?,
        linkUrl: String?,
    ): Notification

    fun publishAll(commands: List<PublishNotificationCommand>): List<Notification>
}

data class PublishNotificationCommand(
    val receiverUserId: UUID,
    val senderUserId: UUID?,
    val type: NotificationType,
    val referenceType: NotificationReferenceType?,
    val referenceId: UUID?,
    val linkUrl: String?,
)
