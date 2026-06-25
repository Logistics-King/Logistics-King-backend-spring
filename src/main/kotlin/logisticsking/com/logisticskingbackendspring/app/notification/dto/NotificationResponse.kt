package logisticsking.com.logisticskingbackendspring.app.notification.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.notification.result.NotificationResult
import logisticsking.com.logisticskingbackendspring.app.notification.result.ReadAllNotificationsResult
import logisticsking.com.logisticskingbackendspring.app.notification.result.UnreadNotificationCountResult
import org.springframework.data.domain.Page
import java.time.LocalDateTime

@Schema(description = "알림 응답")
sealed interface NotificationResponse {
    @Schema(name = "NotificationDetailResponse")
    data class Detail(
        @field:Schema(description = "알림 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val notificationId: String,

        @field:Schema(description = "수신자 사용자 ID", example = "019b1f44-a741-7000-8000-000000000002")
        val receiverUserId: String,

        @field:Schema(description = "발신자 사용자 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val senderUserId: String?,

        @field:Schema(description = "알림 타입", example = "PROPOSAL_SUBMITTED")
        val type: String,

        @field:Schema(description = "알림 제목", example = "새 제안 도착")
        val title: String,

        @field:Schema(description = "알림 메시지", example = "계약 요청에 새 대리점 제안이 도착했습니다.")
        val message: String,

        @field:Schema(description = "프론트 이동 경로", example = "/contract-requests/019b1f44-a741-7000-8000-000000000004/proposals")
        val linkUrl: String?,

        @field:Schema(description = "참조 도메인 타입", example = "PROPOSAL")
        val referenceType: String?,

        @field:Schema(description = "참조 도메인 ID", example = "019b1f44-a741-7000-8000-000000000005")
        val referenceId: String?,

        @field:Schema(description = "읽은 시각. 읽지 않았으면 null")
        val readAt: LocalDateTime?,

        @field:Schema(description = "생성 시각")
        val createdAt: LocalDateTime?,
    ) : NotificationResponse {
        companion object {
            fun from(result: NotificationResult): Detail {
                return Detail(
                    notificationId = result.notificationId.toString(),
                    receiverUserId = result.receiverUserId.toString(),
                    senderUserId = result.senderUserId?.toString(),
                    type = result.type.name,
                    title = result.title,
                    message = result.message,
                    linkUrl = result.linkUrl,
                    referenceType = result.referenceType?.name,
                    referenceId = result.referenceId?.toString(),
                    readAt = result.readAt,
                    createdAt = result.createdAt,
                )
            }
        }
    }

    @Schema(name = "NotificationListResponse")
    data class List(
        @field:Schema(description = "알림 목록")
        val items: kotlin.collections.List<Detail>,

        @field:Schema(description = "현재 페이지 번호. 0부터 시작합니다.", example = "0")
        val page: Int,

        @field:Schema(description = "페이지 크기", example = "20")
        val size: Int,

        @field:Schema(description = "전체 데이터 수", example = "128")
        val totalElements: Long,

        @field:Schema(description = "전체 페이지 수", example = "7")
        val totalPages: Int,

        @field:Schema(description = "다음 페이지 존재 여부", example = "true")
        val hasNext: Boolean,

        @field:Schema(description = "이전 페이지 존재 여부", example = "false")
        val hasPrevious: Boolean,
    ) : NotificationResponse {
        companion object {
            fun from(results: Page<NotificationResult>): List {
                val page = PageResponse.from(results, Detail::from)

                return List(
                    items = page.items,
                    page = page.page,
                    size = page.size,
                    totalElements = page.totalElements,
                    totalPages = page.totalPages,
                    hasNext = page.hasNext,
                    hasPrevious = page.hasPrevious,
                )
            }
        }
    }

    @Schema(name = "UnreadNotificationCountResponse")
    data class UnreadCount(
        @field:Schema(description = "읽지 않은 알림 수", example = "3")
        val count: Long,
    ) : NotificationResponse {
        companion object {
            fun from(result: UnreadNotificationCountResult): UnreadCount {
                return UnreadCount(count = result.count)
            }
        }
    }

    @Schema(name = "ReadAllNotificationsResponse")
    data class ReadAll(
        @field:Schema(description = "읽음 처리된 알림 수", example = "5")
        val readCount: Int,
    ) : NotificationResponse {
        companion object {
            fun from(result: ReadAllNotificationsResult): ReadAll {
                return ReadAll(readCount = result.readCount)
            }
        }
    }
}
