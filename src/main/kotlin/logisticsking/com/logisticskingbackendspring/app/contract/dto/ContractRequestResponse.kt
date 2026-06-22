package logisticsking.com.logisticskingbackendspring.app.contract.dto

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestItemResult
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate

@Schema(description = "계약 요청 응답")
sealed interface ContractRequestResponse {
    @Schema(name = "ContractRequestDetailResponse")
    data class Detail(
        @field:Schema(description = "계약 요청 ID", example = "019b1f44-a741-7000-8000-000000000011")
        val contractRequestId: String,
        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val vendorId: String,
        @field:Schema(description = "대리점 ID. 공개 화주 요청이면 null", example = "019b1f44-a741-7000-8000-000000000002")
        val agencyId: String?,
        @field:Schema(description = "계약 요청 타입", example = "VENDOR_OFFER")
        val type: String,
        @field:Schema(description = "요청자 타입", example = "VENDOR")
        val requesterType: String,
        @field:Schema(description = "요청자 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val requesterId: String,
        @field:Schema(description = "승인자 타입", example = "AGENCY")
        val approverType: String,
        @field:Schema(description = "승인자 ID. 공개 요청이면 null", example = "019b1f44-a741-7000-8000-000000000002")
        val approverId: String?,
        @field:Schema(description = "화주 배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: String?,
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
        @field:Schema(description = "주요 박스 크기", example = "SIZE_60")
        val boxSize: BoxSize,
        @field:Schema(description = "픽업 희망 시작 시간", example = "09:00")
        val pickupStartTime: String,
        @field:Schema(description = "픽업 희망 종료 시간", example = "18:00")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 필요 여부", example = "true")
        val saturdayDeliveryRequired: Boolean,
        @field:Schema(description = "반품 처리 필요 여부", example = "true")
        val returnRequired: Boolean,
        @field:Schema(description = "콜드체인 필요 타입 (NONE, REFRIGERATED, FROZEN)", example = "NONE")
        val coldChainType: ColdChainType,
        @field:Schema(description = "희망 단가", example = "2000")
        val targetUnitPrice: BigDecimal?,
        @field:Schema(description = "요청 메모", example = "의류 중심이며 평일 오전 픽업을 선호합니다.")
        val memo: String?,
        @field:Schema(description = "배송 물품 라인 목록")
        val items: kotlin.collections.List<Item>,
        @field:Schema(description = "계약 요청 상태", example = "OPEN")
        val status: String,
    ) : ContractRequestResponse {
        companion object {
            fun from(result: ContractRequestResult): Detail {
                return Detail(
                    contractRequestId = result.contractRequestId.toString(),
                    vendorId = result.vendorId.toString(),
                    agencyId = result.agencyId?.toString(),
                    type = result.type.name,
                    requesterType = result.requesterType.name,
                    requesterId = result.requesterId.toString(),
                    approverType = result.approverType.name,
                    approverId = result.approverId?.toString(),
                    productId = result.productId?.toString(),
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
                    pickupStartTime = result.pickupStartTime,
                    pickupEndTime = result.pickupEndTime,
                    saturdayDeliveryRequired = result.saturdayDeliveryRequired,
                    returnRequired = result.returnRequired,
                    coldChainType = result.coldChainType,
                    targetUnitPrice = result.targetUnitPrice,
                    memo = result.memo,
                    items = result.items.map(Item::from),
                    status = result.status.name,
                )
            }
        }
    }

    @Schema(name = "ContractRequestItemResponse")
    data class Item(
        @field:Schema(description = "계약 요청 배송 물품 라인 ID", example = "019b1f44-a741-7000-8000-000000000021")
        val itemId: String,
        @field:Schema(description = "화주 배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
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
        @field:Schema(description = "라인 희망 단가", example = "2000")
        val targetUnitPrice: BigDecimal?,
    ) : ContractRequestResponse {
        companion object {
            fun from(result: ContractRequestItemResult): Item {
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
                    targetUnitPrice = result.targetUnitPrice,
                )
            }
        }
    }

    @Schema(name = "ContractRequestListResponse")
    data class List(
        @field:Schema(description = "계약 요청 목록")
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
    ) : ContractRequestResponse {
        companion object {
            fun from(results: Page<ContractRequestResult>): List {
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
