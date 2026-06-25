package logisticsking.com.logisticskingbackendspring.app.contract.dto

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.ContractRequestItemCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestContractType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestType
import logisticsking.com.logisticskingbackendspring.domain.contract.RecurringPickupCycle
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

@Schema(description = "계약 요청")
sealed interface ContractRequestRequest {
    @Schema(name = "ContractRequestCreateRequest")
    data class Create(
        @field:Schema(description = "계약 요청 타입. VENDOR_OFFER는 화주 -> 대리점, AGENCY_OFFER는 대리점 -> 화주", example = "VENDOR_OFFER")
        val type: ContractRequestType = ContractRequestType.VENDOR_OFFER,

        @field:Schema(description = "특정 승인자 ID. VENDOR_OFFER이면 대리점 ID, AGENCY_OFFER이면 화주 ID", example = "019b1f44-a741-7000-8000-000000000004")
        val approverId: UUID? = null,

        @field:Schema(description = "화주 배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: UUID?,

        @field:Schema(description = "픽업 지역", example = "경기도 안산시 일동")
        val pickupRegion: String,

        @field:Schema(description = "픽업 상세 주소", example = "경기도 안산시 상록구 일동 101호")
        val pickupAddress: String?,

        @field:Schema(description = "계약 방식 (SINGLE, RECURRING)", example = "SINGLE")
        val contractType: ContractRequestContractType = ContractRequestContractType.SINGLE,

        @field:Schema(description = "단건 회수 희망 시작일", example = "2026-06-22")
        val pickupDateFrom: LocalDate? = null,

        @field:Schema(description = "단건 회수 희망 종료일", example = "2026-06-26")
        val pickupDateTo: LocalDate? = null,

        @field:Schema(description = "배송 희망 시작일", example = "2026-06-24")
        val deliveryDateFrom: LocalDate? = null,

        @field:Schema(description = "배송 희망 종료일", example = "2026-06-30")
        val deliveryDateTo: LocalDate? = null,

        @field:Schema(description = "정기 회수 주기 (WEEKLY, MONTHLY)", example = "WEEKLY")
        val recurringPickupCycle: RecurringPickupCycle? = null,

        @field:Schema(description = "매주 정기 회수 요일 목록", example = "[\"MONDAY\", \"WEDNESDAY\"]")
        val recurringPickupDaysOfWeek: List<DayOfWeek> = emptyList(),

        @field:Schema(description = "매월 정기 회수 일자", example = "10")
        val recurringPickupDayOfMonth: Int? = null,

        @field:Schema(description = "월 예상 물량", example = "800")
        val monthlyVolume: Int,

        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: ProductCategory,

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

        @field:Schema(description = "배송 물품 라인 목록. 같은 조건끼리 묶어 입력합니다.")
        val items: List<Item>? = null,
    ) : ContractRequestRequest {
        fun toCommand(userId: UUID): CreateContractRequestCommand {
            val effectiveItems = items?.map(Item::toCommand)?.ifEmpty { null }
                ?: listOf(toLegacyItemCommand())

            return CreateContractRequestCommand(
                userId = userId,
                type = type,
                approverId = approverId,
                productId = productId,
                pickupRegion = pickupRegion,
                pickupAddress = pickupAddress,
                contractType = contractType,
                pickupDateFrom = pickupDateFrom,
                pickupDateTo = pickupDateTo,
                deliveryDateFrom = deliveryDateFrom,
                deliveryDateTo = deliveryDateTo,
                recurringPickupCycle = recurringPickupCycle,
                recurringPickupDaysOfWeek = recurringPickupDaysOfWeek,
                recurringPickupDayOfMonth = recurringPickupDayOfMonth,
                monthlyVolume = monthlyVolume,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
                memo = memo,
                items = effectiveItems,
            )
        }

        private fun toLegacyItemCommand(): ContractRequestItemCommand {
            return ContractRequestItemCommand(
                productId = productId,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                boxQuantity = monthlyVolume,
                itemQuantity = 0,
                averageWeightGram = null,
                fragile = false,
                liquid = false,
                freshFood = false,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
            )
        }
    }

    @Schema(name = "ContractRequestUpdateRequest")
    data class Update(
        @field:Schema(description = "화주 배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: UUID?,

        @field:Schema(description = "픽업 지역", example = "경기도 안산시 본오동")
        val pickupRegion: String,

        @field:Schema(description = "픽업 상세 주소", example = "경기도 안산시 상록구 본오동 202호")
        val pickupAddress: String?,

        @field:Schema(description = "계약 방식 (SINGLE, RECURRING)", example = "SINGLE")
        val contractType: ContractRequestContractType = ContractRequestContractType.SINGLE,

        @field:Schema(description = "단건 회수 희망 시작일", example = "2026-06-22")
        val pickupDateFrom: LocalDate? = null,

        @field:Schema(description = "단건 회수 희망 종료일", example = "2026-06-26")
        val pickupDateTo: LocalDate? = null,

        @field:Schema(description = "배송 희망 시작일", example = "2026-06-24")
        val deliveryDateFrom: LocalDate? = null,

        @field:Schema(description = "배송 희망 종료일", example = "2026-06-30")
        val deliveryDateTo: LocalDate? = null,

        @field:Schema(description = "정기 회수 주기 (WEEKLY, MONTHLY)", example = "WEEKLY")
        val recurringPickupCycle: RecurringPickupCycle? = null,

        @field:Schema(description = "매주 정기 회수 요일 목록", example = "[\"MONDAY\", \"WEDNESDAY\"]")
        val recurringPickupDaysOfWeek: List<DayOfWeek> = emptyList(),

        @field:Schema(description = "매월 정기 회수 일자", example = "10")
        val recurringPickupDayOfMonth: Int? = null,

        @field:Schema(description = "월 예상 물량", example = "1000")
        val monthlyVolume: Int,

        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: ProductCategory,

        @field:Schema(description = "품목명", example = "여성 의류")
        val productName: String,

        @field:Schema(description = "주요 박스 크기", example = "SIZE_80")
        val boxSize: BoxSize,

        @field:Schema(description = "픽업 희망 시작 시간", example = "10:00")
        val pickupStartTime: String,

        @field:Schema(description = "픽업 희망 종료 시간", example = "17:00")
        val pickupEndTime: String,

        @field:Schema(description = "토요일 배송 필요 여부", example = "false")
        val saturdayDeliveryRequired: Boolean,

        @field:Schema(description = "반품 처리 필요 여부", example = "true")
        val returnRequired: Boolean,

        @field:Schema(description = "콜드체인 필요 타입 (NONE, REFRIGERATED, FROZEN)", example = "NONE")
        val coldChainType: ColdChainType,

        @field:Schema(description = "희망 단가", example = "2100")
        val targetUnitPrice: BigDecimal?,

        @field:Schema(description = "요청 메모", example = "반품 회수가 자주 발생합니다.")
        val memo: String?,

        @field:Schema(description = "배송 물품 라인 목록. 같은 조건끼리 묶어 입력합니다.")
        val items: List<Item>? = null,
    ) : ContractRequestRequest {
        fun toCommand(
            userId: UUID,
            contractRequestId: UUID,
        ): UpdateContractRequestCommand {
            val effectiveItems = items?.map(Item::toCommand)?.ifEmpty { null }
                ?: listOf(toLegacyItemCommand())

            return UpdateContractRequestCommand(
                userId = userId,
                contractRequestId = contractRequestId,
                productId = productId,
                pickupRegion = pickupRegion,
                pickupAddress = pickupAddress,
                contractType = contractType,
                pickupDateFrom = pickupDateFrom,
                pickupDateTo = pickupDateTo,
                deliveryDateFrom = deliveryDateFrom,
                deliveryDateTo = deliveryDateTo,
                recurringPickupCycle = recurringPickupCycle,
                recurringPickupDaysOfWeek = recurringPickupDaysOfWeek,
                recurringPickupDayOfMonth = recurringPickupDayOfMonth,
                monthlyVolume = monthlyVolume,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
                memo = memo,
                items = effectiveItems,
            )
        }

        private fun toLegacyItemCommand(): ContractRequestItemCommand {
            return ContractRequestItemCommand(
                productId = productId,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                boxQuantity = monthlyVolume,
                itemQuantity = 0,
                averageWeightGram = null,
                fragile = false,
                liquid = false,
                freshFood = false,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
            )
        }
    }

    @Schema(name = "ContractRequestItemRequest")
    data class Item(
        @field:Schema(description = "화주 배송 품목 ID. 기존 품목 템플릿을 참조하지 않는 직접 입력이면 null", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: UUID?,

        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: ProductCategory,

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
    ) : ContractRequestRequest {
        fun toCommand(): ContractRequestItemCommand {
            return ContractRequestItemCommand(
                productId = productId,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                boxQuantity = boxQuantity,
                itemQuantity = itemQuantity,
                averageWeightGram = averageWeightGram,
                fragile = fragile,
                liquid = liquid,
                freshFood = freshFood,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
            )
        }
    }
}
