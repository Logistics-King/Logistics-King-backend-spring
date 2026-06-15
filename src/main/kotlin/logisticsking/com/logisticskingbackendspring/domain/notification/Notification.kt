package logisticsking.com.logisticskingbackendspring.domain.notification

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.time.LocalDateTime
import java.util.UUID

class Notification private constructor(
    val id: UUID,
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

    fun read(readAt: LocalDateTime): Notification {
        if (this.readAt != null) {
            return this
        }

        return restore(
            id = id,
            receiverUserId = receiverUserId,
            senderUserId = senderUserId,
            type = type,
            title = title,
            message = message,
            linkUrl = linkUrl,
            referenceType = referenceType,
            referenceId = referenceId,
            readAt = readAt,
            createdAt = createdAt,
        )
    }

    companion object {
        fun create(
            id: UUID,
            receiverUserId: UUID,
            senderUserId: UUID?,
            type: NotificationType,
            title: String,
            message: String,
            linkUrl: String?,
            referenceType: NotificationReferenceType?,
            referenceId: UUID?,
        ): Notification {
            requireDomain(title.isNotBlank(), NotificationErrorCode.INVALID_TITLE)
            requireDomain(message.isNotBlank(), NotificationErrorCode.INVALID_MESSAGE)

            return Notification(
                id = id,
                receiverUserId = receiverUserId,
                senderUserId = senderUserId,
                type = type,
                title = title.trim(),
                message = message.trim(),
                linkUrl = linkUrl?.trim()?.takeIf { it.isNotBlank() },
                referenceType = referenceType,
                referenceId = referenceId,
                readAt = null,
                createdAt = null,
            )
        }

        fun restore(
            id: UUID,
            receiverUserId: UUID,
            senderUserId: UUID?,
            type: NotificationType,
            title: String,
            message: String,
            linkUrl: String?,
            referenceType: NotificationReferenceType?,
            referenceId: UUID?,
            readAt: LocalDateTime?,
            createdAt: LocalDateTime?,
        ): Notification {
            return Notification(
                id = id,
                receiverUserId = receiverUserId,
                senderUserId = senderUserId,
                type = type,
                title = title,
                message = message,
                linkUrl = linkUrl,
                referenceType = referenceType,
                referenceId = referenceId,
                readAt = readAt,
                createdAt = createdAt,
            )
        }
    }
}
