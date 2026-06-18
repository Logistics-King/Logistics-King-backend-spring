package logisticsking.com.logisticskingbackendspring.infra.persistence.notification

import logisticsking.com.logisticskingbackendspring.domain.notification.Notification
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class NotificationRepositoryImpl(
    private val notificationJpaRepository: NotificationJpaRepository,
) : NotificationRepository {

    override fun save(notification: Notification): Notification {
        return notificationJpaRepository.save(NotificationJpaEntity.from(notification)).toDomain()
    }

    override fun saveAll(notifications: List<Notification>): List<Notification> {
        return notificationJpaRepository.saveAll(
            notifications.map(NotificationJpaEntity::from),
        ).map(NotificationJpaEntity::toDomain)
    }

    override fun findRecentByReceiverUserId(
        receiverUserId: UUID,
        from: LocalDateTime,
        pageable: Pageable,
    ): Page<Notification> {
        return notificationJpaRepository
            .findAllByReceiverUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
                receiverUserId = receiverUserId,
                createdAt = from,
                pageable = pageable,
            )
            .map(NotificationJpaEntity::toDomain)
    }

    override fun countUnreadByReceiverUserId(
        receiverUserId: UUID,
        from: LocalDateTime,
    ): Long {
        return notificationJpaRepository.countByReceiverUserIdAndReadAtIsNullAndCreatedAtGreaterThanEqual(
            receiverUserId = receiverUserId,
            createdAt = from,
        )
    }

    override fun findByIdAndReceiverUserId(
        id: UUID,
        receiverUserId: UUID,
    ): Notification? {
        return notificationJpaRepository.findByIdAndReceiverUserId(id, receiverUserId)?.toDomain()
    }

    override fun markAsRead(
        id: UUID,
        receiverUserId: UUID,
        readAt: LocalDateTime,
    ): Notification? {
        val notification = notificationJpaRepository.findByIdAndReceiverUserId(id, receiverUserId)
            ?: return null
        notification.markAsRead(readAt)

        return notificationJpaRepository.save(notification).toDomain()
    }

    override fun markAllAsRead(
        receiverUserId: UUID,
        readAt: LocalDateTime,
    ): Int {
        val notifications = notificationJpaRepository.findAllByReceiverUserIdAndReadAtIsNull(receiverUserId)
        notifications.forEach { it.markAsRead(readAt) }
        notificationJpaRepository.saveAll(notifications)

        return notifications.size
    }
}
