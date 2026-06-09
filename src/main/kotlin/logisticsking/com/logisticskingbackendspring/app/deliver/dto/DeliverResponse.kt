package logisticsking.com.logisticskingbackendspring.app.deliver.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.deliver.result.DeliverResult
import org.springframework.data.domain.Page

@Schema(description = "배송기사 응답")
sealed interface DeliverResponse {
    @Schema(name = "DeliverDetailResponse")
    data class Detail(
        @field:Schema(description = "배송기사 ID", example = "019b1f44-a741-7000-8000-000000000020")
        val deliverId: String,
        @field:Schema(description = "사용자 ID", example = "019b1f44-a741-7000-8000-000000000021")
        val userId: String,
        @field:Schema(description = "소속 대리점 ID", example = "019b1f44-a741-7000-8000-000000000010")
        val agencyId: String,
        @field:Schema(description = "기사명", example = "김택배")
        val driverName: String,
        @field:Schema(description = "연락처", example = "010-1234-5678")
        val phoneNumber: String,
        @field:Schema(description = "차량번호", example = "12가3456")
        val vehicleNumber: String?,
        @field:Schema(description = "담당 가능 지역")
        val serviceRegions: kotlin.collections.List<String>,
        @field:Schema(description = "운영 활성 여부", example = "true")
        val active: Boolean,
        @field:Schema(description = "메모", example = "오전 집하 담당")
        val memo: String?,
    ) : DeliverResponse {
        companion object {
            fun from(result: DeliverResult): Detail {
                return Detail(
                    deliverId = result.deliverId.toString(),
                    userId = result.userId.toString(),
                    agencyId = result.agencyId.toString(),
                    driverName = result.driverName,
                    phoneNumber = result.phoneNumber,
                    vehicleNumber = result.vehicleNumber,
                    serviceRegions = result.serviceRegions,
                    active = result.active,
                    memo = result.memo,
                )
            }
        }
    }

    @Schema(name = "DeliverListResponse")
    data class List(
        @field:Schema(description = "배송기사 목록")
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
    ) : DeliverResponse {
        companion object {
            fun from(results: Page<DeliverResult>): List {
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
}
