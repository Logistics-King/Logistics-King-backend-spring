package logisticsking.com.logisticskingbackendspring.infra.persistence.notification

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.notification.Notification
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationReferenceType
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationType
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "notifications")
class NotificationJpaEntity private constructor(
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    val id: UUID,

    @Column(name = "receiver_user_id", nullable = false, columnDefinition = "BINARY(16)")
    val receiverUserId: UUID,

    @Column(name = "sender_user_id", nullable = true, columnDefinition = "BINARY(16)")
    val senderUserId: UUID?,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    val type: NotificationType,

    @Column(name = "title", nullable = false, length = 100)
    val title: String,

    @Column(name = "message", nullable = false, length = 500)
    val message: String,

    @Column(name = "link_url", nullable = true, length = 255)
    val linkUrl: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = true, length = 50)
    val referenceType: NotificationReferenceType?,

    @Column(name = "reference_id", nullable = true, columnDefinition = "BINARY(16)")
    val referenceId: UUID?,

    @Column(name = "read_at", nullable = true)
    var readAt: LocalDateTime?,
) : BaseJpaEntity() {

    fun markAsRead(readAt: LocalDateTime) {
        if (this.readAt == null) {
            this.readAt = readAt
        }
    }

    fun toDomain(): Notification {
        return Notification.restore(
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
        fun from(notification: Notification): NotificationJpaEntity {
            return NotificationJpaEntity(
                id = notification.id,
                receiverUserId = notification.receiverUserId,
                senderUserId = notification.senderUserId,
                type = notification.type,
                title = notification.title,
                message = notification.message,
                linkUrl = notification.linkUrl,
                referenceType = notification.referenceType,
                referenceId = notification.referenceId,
                readAt = notification.readAt,
            )
        }
    }
}
