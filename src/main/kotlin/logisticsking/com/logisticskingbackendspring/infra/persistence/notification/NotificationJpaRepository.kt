package logisticsking.com.logisticskingbackendspring.infra.persistence.notification

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime
import java.util.UUID

interface NotificationJpaRepository : JpaRepository<NotificationJpaEntity, UUID> {
    fun findByIdAndReceiverUserId(
        id: UUID,
        receiverUserId: UUID,
    ): NotificationJpaEntity?

    fun findAllByReceiverUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
        receiverUserId: UUID,
        createdAt: LocalDateTime,
        pageable: Pageable,
    ): Page<NotificationJpaEntity>

    fun countByReceiverUserIdAndReadAtIsNullAndCreatedAtGreaterThanEqual(
        receiverUserId: UUID,
        createdAt: LocalDateTime,
    ): Long

    @Query(
        """
        select notification
        from NotificationJpaEntity notification
        where notification.receiverUserId = :receiverUserId
          and notification.createdAt >= :createdAt
        order by notification.createdAt asc, notification.id asc
        """
    )
    fun findAllAfter(
        receiverUserId: UUID,
        createdAt: LocalDateTime,
        pageable: Pageable,
    ): List<NotificationJpaEntity>

    fun findAllByReceiverUserIdAndReadAtIsNull(receiverUserId: UUID): List<NotificationJpaEntity>
}
