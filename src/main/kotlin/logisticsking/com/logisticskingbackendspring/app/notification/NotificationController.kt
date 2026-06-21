package logisticsking.com.logisticskingbackendspring.app.notification

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.notification.dto.NotificationResponse
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.GetMyNotificationsUseCase
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.GetUnreadNotificationCountUseCase
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.ReadAllNotificationsUseCase
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.ReadNotificationUseCase
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.SubscribeNotificationStreamUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.UUID

@Tag(name = "Notification", description = "알림 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.ADMIN, UserRole.VENDOR, UserRole.AGENCY, UserRole.DRIVER])
@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val getMyNotificationsUseCase: GetMyNotificationsUseCase,
    private val getUnreadNotificationCountUseCase: GetUnreadNotificationCountUseCase,
    private val readNotificationUseCase: ReadNotificationUseCase,
    private val readAllNotificationsUseCase: ReadAllNotificationsUseCase,
    private val subscribeNotificationStreamUseCase: SubscribeNotificationStreamUseCase,
) {

    @Operation(
        summary = "내 알림 SSE 스트림 구독",
        description = "로그인한 사용자의 새 알림을 Server-Sent Events로 구독합니다. EventSource 사용 시 cookie 인증을 위해 withCredentials를 사용합니다.",
    )
    @GetMapping("/stream")
    fun stream(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): SseEmitter {
        return subscribeNotificationStreamUseCase.subscribe(user.userId)
    }

    @Operation(summary = "내 알림 목록 조회", description = "로그인한 사용자의 최근 30일 알림 목록을 조회합니다.")
    @GetMapping("/me")
    fun getMyNotifications(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<NotificationResponse.List> {
        val results = getMyNotificationsUseCase.getMyNotifications(user.userId, pageable)

        return ApiResponse.success(
            response = NotificationResponse.List.from(results),
        )
    }

    @Operation(summary = "읽지 않은 알림 수 조회", description = "로그인한 사용자의 최근 30일 미확인 알림 수를 조회합니다.")
    @GetMapping("/me/unread-count")
    fun getUnreadCount(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<NotificationResponse.UnreadCount> {
        val result = getUnreadNotificationCountUseCase.getUnreadCount(user.userId)

        return ApiResponse.success(
            response = NotificationResponse.UnreadCount.from(result),
        )
    }

    @Operation(summary = "알림 읽음 처리", description = "로그인한 사용자의 특정 알림을 읽음 처리합니다.")
    @PutMapping("/{notificationId}/read")
    fun read(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable notificationId: UUID,
    ): ApiResponse<NotificationResponse.Detail> {
        val result = readNotificationUseCase.read(
            userId = user.userId,
            notificationId = notificationId,
        )

        return ApiResponse.success(
            response = NotificationResponse.Detail.from(result),
        )
    }

    @Operation(summary = "내 알림 전체 읽음 처리", description = "로그인한 사용자의 읽지 않은 알림을 모두 읽음 처리합니다.")
    @PutMapping("/me/read-all")
    fun readAll(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<NotificationResponse.ReadAll> {
        val result = readAllNotificationsUseCase.readAll(user.userId)

        return ApiResponse.success(
            response = NotificationResponse.ReadAll.from(result),
        )
    }
}
