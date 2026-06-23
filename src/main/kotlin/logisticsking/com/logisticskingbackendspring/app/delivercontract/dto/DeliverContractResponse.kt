package logisticsking.com.logisticskingbackendspring.app.delivercontract.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.delivercontract.result.DeliverContractResult
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.LocalDate

@Schema(description = "배송기사 계약 응답")
sealed interface DeliverContractResponse {
    @Schema(name = "DeliverContractDetailResponse")
    data class Detail(
        @field:Schema(description = "배송기사 계약 ID", example = "019b1f44-a741-7000-8000-000000000401")
        val deliverContractId: String,

        @field:Schema(description = "대리점 ID", example = "019b1f44-a741-7000-8000-000000000201")
        val agencyId: String,

        @field:Schema(description = "배송기사 ID", example = "019b1f44-a741-7000-8000-000000000301")
        val deliverId: String,

        @field:Schema(description = "계약 담당 지역", example = "경기도 안산시 일동")
        val serviceRegion: String,

        @field:Schema(description = "예상 월 배정 물량", example = "800")
        val expectedMonthlyVolume: Int,

        @field:Schema(description = "기사 지급 건당 단가", example = "900")
        val unitPrice: BigDecimal,

        @field:Schema(description = "계약 시작일", example = "2026-06-01")
        val startDate: LocalDate,

        @field:Schema(description = "계약 종료일", example = "2026-12-31")
        val endDate: LocalDate?,

        @field:Schema(description = "계약 메모", example = "일동 의류 물량 오전 집하 담당")
        val memo: String?,

        @field:Schema(description = "계약 상태", example = "REQUESTED")
        val status: String,
    ) : DeliverContractResponse {
        companion object {
            fun from(result: DeliverContractResult): Detail {
                return Detail(
                    deliverContractId = result.deliverContractId.toString(),
                    agencyId = result.agencyId.toString(),
                    deliverId = result.deliverId.toString(),
                    serviceRegion = result.serviceRegion,
                    expectedMonthlyVolume = result.expectedMonthlyVolume,
                    unitPrice = result.unitPrice,
                    startDate = result.startDate,
                    endDate = result.endDate,
                    memo = result.memo,
                    status = result.status.name,
                )
            }
        }
    }

    @Schema(name = "DeliverContractListResponse")
    data class List(
        @field:Schema(description = "배송기사 계약 목록")
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
    ) : DeliverContractResponse {
        companion object {
            fun from(results: Page<DeliverContractResult>): List {
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
