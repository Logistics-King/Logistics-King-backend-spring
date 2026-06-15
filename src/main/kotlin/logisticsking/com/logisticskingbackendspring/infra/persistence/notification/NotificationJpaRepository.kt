package logisticsking.com.logisticskingbackendspring.infra.persistence.notification

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
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

    fun findAllByReceiverUserIdAndReadAtIsNull(receiverUserId: UUID): List<NotificationJpaEntity>
}
