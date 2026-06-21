package logisticsking.com.logisticskingbackendspring.infra.notification

import logisticsking.com.logisticskingbackendspring.app.notification.dto.NotificationResponse
import logisticsking.com.logisticskingbackendspring.app.notification.result.NotificationResult
import logisticsking.com.logisticskingbackendspring.app.notification.usecase.SubscribeNotificationStreamUseCase
import logisticsking.com.logisticskingbackendspring.domain.notification.Notification
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationStreamSender
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class NotificationSseStreamService : NotificationStreamSender,
    SubscribeNotificationStreamUseCase {

    private val emittersByUserId = ConcurrentHashMap<UUID, CopyOnWriteArrayList<SseEmitter>>()

    override fun subscribe(userId: UUID): SseEmitter {
        val emitter = SseEmitter(TIMEOUT_MILLIS)
        val emitters = emittersByUserId.computeIfAbsent(userId) { CopyOnWriteArrayList() }

        emitters.add(emitter)

        // SSE 연결은 브라우저 탭, 네트워크, 서버 timeout 등으로 자주 끊길 수 있다.
        // 완료/timeout/error 모든 경로에서 registry를 정리해야 끊긴 emitter가 계속 쌓이지 않는다.
        emitter.onCompletion { removeEmitter(userId, emitter) }
        emitter.onTimeout {
            removeEmitter(userId, emitter)
            emitter.complete()
        }
        emitter.onError {
            removeEmitter(userId, emitter)
        }

        // 연결 직후 작은 이벤트를 보내면 프론트가 "연결됨" 상태를 즉시 판단할 수 있고,
        // 일부 프록시 환경에서 빈 연결이 바로 닫히는 문제도 줄일 수 있다.
        sendToEmitter(
            userId = userId,
            emitter = emitter,
            event = SseEmitter.event()
                .name(CONNECTED_EVENT_NAME)
                .data(mapOf("connected" to true)),
        )

        return emitter
    }

    override fun send(notification: Notification) {
        val emitters = emittersByUserId[notification.receiverUserId] ?: return
        val response = NotificationResponse.Detail.from(NotificationResult.from(notification))
        val event = SseEmitter.event()
            .id(notification.id.toString())
            .name(NOTIFICATION_EVENT_NAME)
            .data(response)

        // CopyOnWriteArrayList를 쓰는 이유는 같은 userId의 여러 탭에 push하는 동안,
        // 실패한 emitter를 제거해도 순회가 안전하게 유지되기 때문이다.
        emitters.forEach { emitter ->
            sendToEmitter(
                userId = notification.receiverUserId,
                emitter = emitter,
                event = event,
            )
        }
    }

    private fun sendToEmitter(
        userId: UUID,
        emitter: SseEmitter,
        event: SseEmitter.SseEventBuilder,
    ) {
        try {
            emitter.send(event)
        } catch (exception: IOException) {
            removeEmitter(userId, emitter)
            emitter.completeWithError(exception)
        } catch (exception: IllegalStateException) {
            removeEmitter(userId, emitter)
            emitter.completeWithError(exception)
        }
    }

    private fun removeEmitter(
        userId: UUID,
        emitter: SseEmitter,
    ) {
        val emitters = emittersByUserId[userId] ?: return
        emitters.remove(emitter)
        if (emitters.isEmpty()) {
            emittersByUserId.remove(userId, emitters)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 30 * 60 * 1000L
        private const val CONNECTED_EVENT_NAME = "connected"
        private const val NOTIFICATION_EVENT_NAME = "notification"
    }
}
