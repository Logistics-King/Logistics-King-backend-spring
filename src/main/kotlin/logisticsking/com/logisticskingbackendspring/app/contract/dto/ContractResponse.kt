package logisticsking.com.logisticskingbackendspring.app.contract.dto

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.agency.dto.AgencyResponse
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractItemResult
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractResult
import logisticsking.com.logisticskingbackendspring.app.vendor.dto.VendorResponse
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate

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
        @field:Schema(description = "계약 방식 (SINGLE, RECURRING)", example = "SINGLE")
        val contractType: String,
        @field:Schema(description = "단건 회수 희망 시작일", example = "2026-06-22")
        val pickupDateFrom: LocalDate?,
        @field:Schema(description = "단건 회수 희망 종료일", example = "2026-06-26")
        val pickupDateTo: LocalDate?,
        @field:Schema(description = "배송 희망 시작일", example = "2026-06-24")
        val deliveryDateFrom: LocalDate?,
        @field:Schema(description = "배송 희망 종료일", example = "2026-06-30")
        val deliveryDateTo: LocalDate?,
        @field:Schema(description = "정기 회수 주기 (WEEKLY, MONTHLY)", example = "WEEKLY")
        val recurringPickupCycle: String?,
        @field:Schema(description = "매주 정기 회수 요일 목록", example = "[\"MONDAY\", \"WEDNESDAY\"]")
        val recurringPickupDaysOfWeek: kotlin.collections.List<DayOfWeek>,
        @field:Schema(description = "매월 정기 회수 일자", example = "10")
        val recurringPickupDayOfMonth: Int?,
        @field:Schema(description = "월 예상 물량", example = "800")
        val monthlyVolume: Int,
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: String,
        @field:Schema(description = "품목명", example = "일반 의류")
        val productName: String,
        @field:Schema(description = "박스 크기", example = "SIZE_60")
        val boxSize: BoxSize,
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
        @field:Schema(description = "지원 콜드체인 타입 (NONE, REFRIGERATED, FROZEN)", example = "REFRIGERATED")
        val coldChainType: ColdChainType,
        @field:Schema(description = "계약 메모", example = "의류 800박스 기준 집하 가능")
        val memo: String?,
        @field:Schema(description = "계약 당시 배송 물품 라인 스냅샷")
        val items: kotlin.collections.List<Item>,
        @field:Schema(description = "계약 상태", example = "ACTIVE")
        val status: String,
        @field:Schema(description = "계약 화주 요약 정보")
        val vendor: VendorResponse.Summary?,
        @field:Schema(description = "계약 대리점 요약 정보")
        val agency: AgencyResponse.Summary?,
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
                    contractType = result.contractType.name,
                    pickupDateFrom = result.pickupDateFrom,
                    pickupDateTo = result.pickupDateTo,
                    deliveryDateFrom = result.deliveryDateFrom,
                    deliveryDateTo = result.deliveryDateTo,
                    recurringPickupCycle = result.recurringPickupCycle?.name,
                    recurringPickupDaysOfWeek = result.recurringPickupDaysOfWeek,
                    recurringPickupDayOfMonth = result.recurringPickupDayOfMonth,
                    monthlyVolume = result.monthlyVolume,
                    productCategory = result.productCategory.name,
                    productName = result.productName,
                    boxSize = result.boxSize,
                    unitPrice = result.unitPrice,
                    pickupStartTime = result.pickupStartTime,
                    pickupEndTime = result.pickupEndTime,
                    saturdayDeliveryAvailable = result.saturdayDeliveryAvailable,
                    returnAvailable = result.returnAvailable,
                    coldChainType = result.coldChainType,
                    memo = result.memo,
                    items = result.items.map(Item::from),
                    status = result.status.name,
                    vendor = result.vendor?.let(VendorResponse.Summary::from),
                    agency = result.agency?.let(AgencyResponse.Summary::from),
                )
            }
        }
    }

    @Schema(name = "ContractItemResponse")
    data class Item(
        @field:Schema(description = "계약 배송 물품 라인 ID", example = "019b1f44-a741-7000-8000-000000000521")
        val itemId: String,
        @field:Schema(description = "원본 화주 배송 품목 ID. 직접 입력 품목이면 null", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: String?,
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: String,
        @field:Schema(description = "품목명", example = "일반 의류")
        val productName: String,
        @field:Schema(description = "박스 크기", example = "SIZE_60")
        val boxSize: BoxSize,
        @field:Schema(description = "박스 수량", example = "6")
        val boxQuantity: Int,
        @field:Schema(description = "낱개 수량", example = "0")
        val itemQuantity: Int,
        @field:Schema(description = "평균 무게(g)", example = "700")
        val averageWeightGram: Int?,
        @field:Schema(description = "파손 주의 여부", example = "false")
        val fragile: Boolean,
        @field:Schema(description = "액체 포함 여부", example = "false")
        val liquid: Boolean,
        @field:Schema(description = "신선식품 여부", example = "false")
        val freshFood: Boolean,
        @field:Schema(description = "콜드체인 필요 타입", example = "NONE")
        val coldChainType: ColdChainType,
        @field:Schema(description = "계약 당시 확정 단가", example = "2050")
        val unitPrice: BigDecimal,
    ) : ContractResponse {
        companion object {
            fun from(result: ContractItemResult): Item {
                return Item(
                    itemId = result.itemId.toString(),
                    productId = result.productId?.toString(),
                    productCategory = result.productCategory.name,
                    productName = result.productName,
                    boxSize = result.boxSize,
                    boxQuantity = result.boxQuantity,
                    itemQuantity = result.itemQuantity,
                    averageWeightGram = result.averageWeightGram,
                    fragile = result.fragile,
                    liquid = result.liquid,
                    freshFood = result.freshFood,
                    coldChainType = result.coldChainType,
                    unitPrice = result.unitPrice,
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
