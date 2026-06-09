package logisticsking.com.logisticskingbackendspring.app.contract.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractResult
import org.springframework.data.domain.Page
import java.math.BigDecimal

@Schema(description = "최종 계약 응답")
sealed interface ContractResponse {
    @Schema(name = "ContractDetailResponse")
    data class Detail(
        @field:Schema(description = "계약 ID", example = "019b1f44-a741-7000-8000-000000000501")
        val contractId: String,
        @field:Schema(description = "계약 요청 ID", example = "019b1f44-a741-7000-8000-000000000101")
        val contractRequestId: String,
        @field:Schema(description = "선택된 제안 ID", example = "019b1f44-a741-7000-8000-000000000201")
        val proposalId: String,
        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000301")
        val vendorId: String,
        @field:Schema(description = "대리점 ID", example = "019b1f44-a741-7000-8000-000000000401")
        val agencyId: String,
        @field:Schema(description = "픽업 지역", example = "경기도 안산시 일동")
        val pickupRegion: String,
        @field:Schema(description = "픽업 상세 주소", example = "경기도 안산시 상록구 일동 101호")
        val pickupAddress: String?,
        @field:Schema(description = "월 예상 물량", example = "800")
        val monthlyVolume: Int,
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: String,
        @field:Schema(description = "품목명", example = "일반 의류")
        val productName: String,
        @field:Schema(description = "박스 크기", example = "60")
        val boxSize: String,
        @field:Schema(description = "확정 건당 단가", example = "2050")
        val unitPrice: BigDecimal,
        @field:Schema(description = "확정 픽업 시작 시간", example = "10:00")
        val pickupStartTime: String,
        @field:Schema(description = "확정 픽업 종료 시간", example = "17:00")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 가능 여부", example = "true")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 회수 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "냉장/냉동 가능 여부", example = "false")
        val coldChainAvailable: Boolean,
        @field:Schema(description = "계약 메모", example = "의류 800박스 기준 집하 가능")
        val memo: String?,
        @field:Schema(description = "계약 상태", example = "ACTIVE")
        val status: String,
    ) : ContractResponse {
        companion object {
            fun from(result: ContractResult): Detail {
                return Detail(
                    contractId = result.contractId.toString(),
                    contractRequestId = result.contractRequestId.toString(),
                    proposalId = result.proposalId.toString(),
                    vendorId = result.vendorId.toString(),
                    agencyId = result.agencyId.toString(),
                    pickupRegion = result.pickupRegion,
                    pickupAddress = result.pickupAddress,
                    monthlyVolume = result.monthlyVolume,
                    productCategory = result.productCategory.name,
                    productName = result.productName,
                    boxSize = result.boxSize,
                    unitPrice = result.unitPrice,
                    pickupStartTime = result.pickupStartTime,
                    pickupEndTime = result.pickupEndTime,
                    saturdayDeliveryAvailable = result.saturdayDeliveryAvailable,
                    returnAvailable = result.returnAvailable,
                    coldChainAvailable = result.coldChainAvailable,
                    memo = result.memo,
                    status = result.status.name,
                )
            }
        }
    }

    @Schema(name = "ContractListResponse")
    data class List(
        @field:Schema(description = "최종 계약 목록")
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
    ) : ContractResponse {
        companion object {
            fun from(results: Page<ContractResult>): List {
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
