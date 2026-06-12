package logisticsking.com.logisticskingbackendspring.app.proposal.dto

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalResult
import org.springframework.data.domain.Page
import java.math.BigDecimal

@Schema(description = "제안 응답")
sealed interface ProposalResponse {
    @Schema(name = "ProposalDetailResponse")
    data class Detail(
        @field:Schema(description = "제안 ID", example = "019b1f44-a741-7000-8000-000000000101")
        val proposalId: String,
        @field:Schema(description = "계약 요청 ID", example = "019b1f44-a741-7000-8000-000000000011")
        val contractRequestId: String,
        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val vendorId: String,
        @field:Schema(description = "대리점 ID", example = "019b1f44-a741-7000-8000-000000000010")
        val agencyId: String,
        @field:Schema(description = "건당 제안 단가", example = "2050")
        val unitPrice: BigDecimal,
        @field:Schema(description = "제안 픽업 시작 시간", example = "10:00")
        val pickupStartTime: String,
        @field:Schema(description = "제안 픽업 종료 시간", example = "17:00")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 가능 여부", example = "true")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 회수 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "지원 콜드체인 타입 (NONE, REFRIGERATED, FROZEN)", example = "REFRIGERATED")
        val coldChainType: ColdChainType,
        @field:Schema(description = "제안 메모", example = "의류 800박스 기준 집하 가능합니다.")
        val memo: String?,
        @field:Schema(description = "제안 상태", example = "SUBMITTED")
        val status: String,
    ) : ProposalResponse {
        companion object {
            fun from(result: ProposalResult): Detail {
                return Detail(
                    proposalId = result.proposalId.toString(),
                    contractRequestId = result.contractRequestId.toString(),
                    vendorId = result.vendorId.toString(),
                    agencyId = result.agencyId.toString(),
                    unitPrice = result.unitPrice,
                    pickupStartTime = result.pickupStartTime,
                    pickupEndTime = result.pickupEndTime,
                    saturdayDeliveryAvailable = result.saturdayDeliveryAvailable,
                    returnAvailable = result.returnAvailable,
                    coldChainType = result.coldChainType,
                    memo = result.memo,
                    status = result.status.name,
                )
            }
        }
    }

    @Schema(name = "ProposalListResponse")
    data class List(
        @field:Schema(description = "제안 목록")
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
    ) : ProposalResponse {
        companion object {
            fun from(results: Page<ProposalResult>): List {
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
