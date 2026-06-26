package logisticsking.com.logisticskingbackendspring.domain.notification

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.UUID

interface NotificationRepository {
    fun save(notification: Notification): Notification

    fun saveAll(notifications: List<Notification>): List<Notification>

    fun findRecentByReceiverUserId(
        receiverUserId: UUID,
        from: LocalDateTime,
        pageable: Pageable,
    ): Page<Notification>

    fun countUnreadByReceiverUserId(
        receiverUserId: UUID,
        from: LocalDateTime,
    ): Long

    fun findByIdAndReceiverUserId(
        id: UUID,
        receiverUserId: UUID,
    ): Notification?

    fun findAfter(
        receiverUserId: UUID,
        lastNotification: Notification,
        limit: Int,
    ): List<Notification>

    fun markAsRead(
        id: UUID,
        receiverUserId: UUID,
        readAt: LocalDateTime,
    ): Notification?

    fun markAllAsRead(
        receiverUserId: UUID,
        readAt: LocalDateTime,
    ): Int
}
