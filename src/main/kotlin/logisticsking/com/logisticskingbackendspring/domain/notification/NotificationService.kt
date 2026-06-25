package logisticsking.com.logisticskingbackendspring.domain.notification

import logisticsking.com.logisticskingbackendspring.app.notification.result.NotificationResult
import logisticsking.com.logisticskingbackendspring.app.notification.result.ReadAllNotificationsResult
import logisticsking.com.logisticskingbackendspring.app.notification.result.UnreadNotificationCountResult
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.GetMyNotificationsUseCase
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.GetUnreadNotificationCountUseCase
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.ReadAllNotificationsUseCase
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.ReadNotificationUseCase
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.LocalDateTime
import java.util.UUID

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationStreamSender: NotificationStreamSender,
    private val idGenerator: IdGenerator,
) : NotificationPublisher,
    GetMyNotificationsUseCase,
    GetUnreadNotificationCountUseCase,
    ReadNotificationUseCase,
    ReadAllNotificationsUseCase {

    @Transactional
    override fun publish(
        receiverUserId: UUID,
        senderUserId: UUID?,
        type: NotificationType,
        referenceType: NotificationReferenceType?,
        referenceId: UUID?,
        linkUrl: String?,
    ): Notification {
        val template = NotificationTemplate.of(type)
        val notification = Notification.create(
            id = idGenerator.generate(),
            receiverUserId = receiverUserId,
            senderUserId = senderUserId,
            type = type,
            title = template.title,
            message = template.message,
            linkUrl = linkUrl,
            referenceType = referenceType,
            referenceId = referenceId,
        )

        val saved = notificationRepository.save(notification)
        // 알림의 원천 상태는 RDB다. SSE는 사용자가 화면을 새로고침하지 않아도
        // 새 알림을 즉시 알 수 있게 하는 전달 채널이다. 트랜잭션 커밋 전송이면
        // 커밋 실패 시 저장되지 않은 알림이 화면에 먼저 보일 수 있으므로 afterCommit에 태운다.
        dispatchAfterCommit {
            notificationStreamSender.send(saved)
        }

        return saved
    }

    @Transactional
    override fun publishAll(commands: List<PublishNotificationCommand>): List<Notification> {
        if (commands.isEmpty()) {
            return emptyList()
        }

        val notifications = commands.map { command ->
            val template = NotificationTemplate.of(command.type)
            Notification.create(
                id = idGenerator.generate(),
                receiverUserId = command.receiverUserId,
                senderUserId = command.senderUserId,
                type = command.type,
                title = template.title,
                message = template.message,
                linkUrl = command.linkUrl,
                referenceType = command.referenceType,
                referenceId = command.referenceId,
            )
        }

        val savedNotifications = notificationRepository.saveAll(notifications)
        // 여러 알림을 한 트랜잭션에서 저장한 뒤 수신자별 SSE 연결로 나눠 보낸다.
        // 전송 실패는 연결 정리 대상일 뿐, 이미 저장된 알림 이력을 롤백할 이유가 없다.
        dispatchAfterCommit {
            notificationStreamSender.sendAll(savedNotifications)
        }

        return savedNotifications
    }

    @Transactional(readOnly = true)
    override fun getMyNotifications(
        userId: UUID,
        pageable: Pageable,
    ): Page<NotificationResult> {
        return notificationRepository.findRecentByReceiverUserId(
            receiverUserId = userId,
            from = LocalDateTime.now().minusDays(RECENT_DAYS),
            pageable = pageable,
        ).map(NotificationResult::from)
    }

    @Transactional(readOnly = true)
    override fun getUnreadCount(userId: UUID): UnreadNotificationCountResult {
        return UnreadNotificationCountResult(
            count = notificationRepository.countUnreadByReceiverUserId(
                receiverUserId = userId,
                from = LocalDateTime.now().minusDays(RECENT_DAYS),
            ),
        )
    }

    @Transactional
    override fun read(
        userId: UUID,
        notificationId: UUID,
    ): NotificationResult {
        val notification = notificationRepository.markAsRead(
            id = notificationId,
            receiverUserId = userId,
            readAt = LocalDateTime.now(),
        ) ?: throw GlobalException(NotificationErrorCode.NOT_FOUND)

        return NotificationResult.from(notification)
    }

    @Transactional
    override fun readAll(userId: UUID): ReadAllNotificationsResult {
        return ReadAllNotificationsResult(
            readCount = notificationRepository.markAllAsRead(
                receiverUserId = userId,
                readAt = LocalDateTime.now(),
            ),
        )
    }

    companion object {
        private const val RECENT_DAYS = 30L
    }

    private fun dispatchAfterCommit(action: () -> Unit) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action()
            return
        }

        TransactionSynchronizationManager.registerSynchronization(
            object : TransactionSynchronization {
                override fun afterCommit() {
                    action()
                }
            }
        )
    }
}

private data class NotificationTemplate(
    val title: String,

    val message: String,
) {
    companion object {
        fun of(type: NotificationType): NotificationTemplate {
            return when (type) {
                NotificationType.CONTRACT_REQUEST_CREATED -> NotificationTemplate(
                    title = "계약 요청 등록",
                    message = "새 계약 요청이 등록되었습니다.",
                )
                NotificationType.PROPOSAL_SUBMITTED -> NotificationTemplate(
                    title = "새 제안 도착",
                    message = "계약 요청에 새 대리점 제안이 도착했습니다.",
                )
                NotificationType.PROPOSAL_UPDATED -> NotificationTemplate(
                    title = "제안 수정",
                    message = "대리점 제안 조건이 수정되었습니다.",
                )
                NotificationType.PROPOSAL_WITHDRAWN -> NotificationTemplate(
                    title = "제안 철회",
                    message = "대리점 제안이 철회되었습니다.",
                )
                NotificationType.PROPOSAL_ACCEPTED -> NotificationTemplate(
                    title = "제안 수락",
                    message = "화주가 제안을 수락했습니다.",
                )
                NotificationType.PROPOSAL_REJECTED -> NotificationTemplate(
                    title = "제안 미선택",
                    message = "화주가 다른 제안을 선택했습니다.",
                )
                NotificationType.CONTRACT_CREATED -> NotificationTemplate(
                    title = "계약 확정",
                    message = "최종 계약이 확정되었습니다.",
                )
                NotificationType.CONTRACT_CANCELED -> NotificationTemplate(
                    title = "계약 취소",
                    message = "계약이 취소되었습니다.",
                )
                NotificationType.DELIVER_CONTRACT_REQUESTED -> NotificationTemplate(
                    title = "배송기사 계약 요청",
                    message = "대리점에서 배송기사 계약을 요청했습니다.",
                )
                NotificationType.DELIVER_CONTRACT_ACCEPTED -> NotificationTemplate(
                    title = "배송기사 계약 수락",
                    message = "배송기사가 계약 요청을 수락했습니다.",
                )
                NotificationType.DELIVER_CONTRACT_REJECTED -> NotificationTemplate(
                    title = "배송기사 계약 거절",
                    message = "배송기사가 계약 요청을 거절했습니다.",
                )
            }
        }
    }
}
