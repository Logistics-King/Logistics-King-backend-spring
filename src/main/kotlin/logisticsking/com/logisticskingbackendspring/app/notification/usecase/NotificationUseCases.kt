package logisticsking.com.logisticskingbackendspring.app.notification.usecase

import logisticsking.com.logisticskingbackendspring.app.notification.result.NotificationResult
import logisticsking.com.logisticskingbackendspring.app.notification.result.ReadAllNotificationsResult
import logisticsking.com.logisticskingbackendspring.app.notification.result.UnreadNotificationCountResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID

interface GetMyNotificationsUseCase {
    fun getMyNotifications(
        userId: UUID,
        pageable: Pageable,
    ): Page<NotificationResult>
}

interface GetUnreadNotificationCountUseCase {
    fun getUnreadCount(userId: UUID): UnreadNotificationCountResult
}

interface ReadNotificationUseCase {
    fun read(
        userId: UUID,
        notificationId: UUID,
    ): NotificationResult
}

interface ReadAllNotificationsUseCase {
    fun readAll(userId: UUID): ReadAllNotificationsResult
}

interface SubscribeNotificationStreamUseCase {
    fun subscribe(
        userId: UUID,
        lastEventId: UUID?,
    ): SseEmitter
}
