package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class ContractRequest private constructor(
    // 계약 요청 식별자.
    val id: UUID,
    // 계약 요청 타입. VENDOR_OFFER는 화주 -> 대리점, AGENCY_OFFER는 대리점 -> 화주.
    val type: ContractRequestType,
    // 계약 요청을 시작한 주체 타입.
    val requesterType: ContractPartyType,
    // 계약 요청을 시작한 주체 식별자.
    val requesterId: UUID,
    // 계약 요청을 승인할 주체 타입.
    val approverType: ContractPartyType,
    // 특정 승인자가 정해진 경우의 식별자. 공개 요청이면 null.
    val approverId: UUID?,
    // 화주가 미리 등록한 배송 품목 식별자. 직접 입력 요청이면 null.
    val productId: UUID?,
    // 대리점 매칭에 사용할 픽업 가능 지역.
    val pickupRegion: String,
    // 실제 집하가 필요한 상세 주소.
    val pickupAddress: String?,
    // 단건/정기 계약 요청 구분.
    val contractType: ContractRequestContractType,
    // 단건 계약 요청의 회수 희망 시작일.
    val pickupDateFrom: LocalDate?,
    // 단건 계약 요청의 회수 희망 종료일.
    val pickupDateTo: LocalDate?,
    // 단건 계약 요청의 배송 희망 시작일.
    val deliveryDateFrom: LocalDate?,
    // 단건 계약 요청의 배송 희망 종료일.
    val deliveryDateTo: LocalDate?,
    // 정기 계약 요청의 반복 회수 주기.
    val recurringPickupCycle: RecurringPickupCycle?,
    // 매주 반복 회수 요일 목록.
    val recurringPickupDaysOfWeek: List<DayOfWeek>,
    // 매월 반복 회수 일자.
    val recurringPickupDayOfMonth: Int?,
    // 화주가 한 달에 보낼 것으로 예상하는 박스 수.
    val monthlyVolume: Int,
    // 대리점이 배송 조건과 단가를 판단할 품목 카테고리.
    val productCategory: ProductCategory,
    // 화주가 보내려는 대표 품목명.
    val productName: String,
    // 대리점 단가 산정에 사용할 주요 박스 규격.
    val boxSize: BoxSize,
    // 화주가 원하는 집하 시작 시간.
    val pickupStartTime: String,
    // 화주가 원하는 집하 종료 시간.
    val pickupEndTime: String,
    // 토요일 배송 조건이 필요한지 여부.
    val saturdayDeliveryRequired: Boolean,
    // 반품 회수 조건이 필요한지 여부.
    val returnRequired: Boolean,
    // 필요한 콜드체인 조건. NONE, REFRIGERATED, FROZEN 중 하나.
    val coldChainType: ColdChainType,
    // 화주가 기대하는 건당 희망 단가. 확정 운임은 아님.
    val targetUnitPrice: BigDecimal?,
    // 대리점이 제안할 때 참고할 추가 요청 사항.
    val memo: String?,
    // 같은 배송 조건으로 묶은 배송 물품 라인 목록.
    val items: List<ContractRequestItem>,
    // 계약 요청 진행 상태.
    val status: ContractRequestStatus,
) {
    val vendorId: UUID
        get() = when (ContractPartyType.VENDOR) {
            requesterType -> requesterId
            approverType -> requireNotNull(approverId) { "화주 승인자 ID가 필요합니다." }
            else -> error("계약 요청에 화주가 없습니다.")
        }

    val agencyId: UUID?
        get() = when (ContractPartyType.AGENCY) {
            requesterType -> requesterId
            approverType -> approverId
            else -> null
        }

    fun isRequester(
        partyType: ContractPartyType,
        partyId: UUID,
    ): Boolean {
        return requesterType == partyType && requesterId == partyId
    }

    fun isParticipant(
        partyType: ContractPartyType,
        partyId: UUID,
    ): Boolean {
        return isRequester(partyType, partyId) ||
            (approverType == partyType && (approverId == null || approverId == partyId))
    }

    fun update(
        productId: UUID?,
        pickupRegion: String,
        pickupAddress: String?,
        contractType: ContractRequestContractType = this.contractType,
        pickupDateFrom: LocalDate? = this.pickupDateFrom,
        pickupDateTo: LocalDate? = this.pickupDateTo,
        deliveryDateFrom: LocalDate? = this.deliveryDateFrom,
        deliveryDateTo: LocalDate? = this.deliveryDateTo,
        recurringPickupCycle: RecurringPickupCycle? = this.recurringPickupCycle,
        recurringPickupDaysOfWeek: List<DayOfWeek> = this.recurringPickupDaysOfWeek,
        recurringPickupDayOfMonth: Int? = this.recurringPickupDayOfMonth,
        monthlyVolume: Int,
        productCategory: ProductCategory,
        productName: String,
        boxSize: BoxSize,
        pickupStartTime: String,
        pickupEndTime: String,
        saturdayDeliveryRequired: Boolean,
        returnRequired: Boolean,
        coldChainType: ColdChainType,
        targetUnitPrice: BigDecimal?,
        memo: String?,
        items: List<ContractRequestItem>,
    ): ContractRequest {
        requireDomain(
            status != ContractRequestStatus.CANCELED,
            ContractRequestErrorCode.CANCELED_REQUEST_CANNOT_BE_UPDATED,
        )
        requireDomain(
            status != ContractRequestStatus.CONTRACTED,
            ContractRequestErrorCode.CONTRACTED_REQUEST_CANNOT_BE_UPDATED,
        )
        requireDomain(
            status != ContractRequestStatus.REJECTED,
            ContractRequestErrorCode.REJECTED_REQUEST_CANNOT_BE_UPDATED,
        )

        return create(
            id = id,
            type = type,
            requesterId = requesterId,
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
            items = items,
            status = status,
        )
    }

    fun cancel(): ContractRequest {
        requireDomain(
            status != ContractRequestStatus.CONTRACTED,
            ContractRequestErrorCode.CONTRACTED_REQUEST_CANNOT_BE_CANCELED,
        )
        requireDomain(
            status != ContractRequestStatus.REJECTED,
            ContractRequestErrorCode.REJECTED_REQUEST_CANNOT_BE_CANCELED,
        )

        return restore(
            id = id,
            type = type,
            requesterType = requesterType,
            requesterId = requesterId,
            approverType = approverType,
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
            items = items,
            status = ContractRequestStatus.CANCELED,
        )
    }

    fun contract(): ContractRequest {
        requireDomain(
            status == ContractRequestStatus.OPEN,
            ContractRequestErrorCode.ONLY_OPEN_REQUEST_CAN_BE_CONTRACTED,
        )

        return restore(
            id = id,
            type = type,
            requesterType = requesterType,
            requesterId = requesterId,
            approverType = approverType,
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
            items = items,
            status = ContractRequestStatus.CONTRACTED,
        )
    }

    fun reject(): ContractRequest {
        requireDomain(
            status == ContractRequestStatus.OPEN,
            ContractRequestErrorCode.ONLY_OPEN_REQUEST_CAN_BE_REJECTED,
        )

        return restore(
            id = id,
            type = type,
            requesterType = requesterType,
            requesterId = requesterId,
            approverType = approverType,
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
            items = items,
            status = ContractRequestStatus.REJECTED,
        )
    }

    companion object {
        fun create(
            id: UUID,
            type: ContractRequestType,
            requesterId: UUID,
            approverId: UUID?,
            productId: UUID?,
            pickupRegion: String,
            pickupAddress: String?,
            contractType: ContractRequestContractType = ContractRequestContractType.SINGLE,
            pickupDateFrom: LocalDate? = null,
            pickupDateTo: LocalDate? = null,
            deliveryDateFrom: LocalDate? = null,
            deliveryDateTo: LocalDate? = null,
            recurringPickupCycle: RecurringPickupCycle? = null,
            recurringPickupDaysOfWeek: List<DayOfWeek> = emptyList(),
            recurringPickupDayOfMonth: Int? = null,
            monthlyVolume: Int,
            productCategory: ProductCategory,
            productName: String,
            boxSize: BoxSize,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryRequired: Boolean,
            returnRequired: Boolean,
            coldChainType: ColdChainType,
            targetUnitPrice: BigDecimal?,
            memo: String?,
            items: List<ContractRequestItem>,
            status: ContractRequestStatus = ContractRequestStatus.OPEN,
        ): ContractRequest {
            requireDomain(
                approverId != requesterId,
                ContractRequestErrorCode.INVALID_CONTRACT_PARTY,
            )
            requireDomain(pickupRegion.isNotBlank(), ContractRequestErrorCode.INVALID_PICKUP_REGION)
            requireDomain(monthlyVolume > 0, ContractRequestErrorCode.INVALID_MONTHLY_VOLUME)
            requireDomain(productName.isNotBlank(), ContractRequestErrorCode.INVALID_PRODUCT_NAME)
            requireDomain(items.isNotEmpty(), ContractRequestErrorCode.INVALID_ITEMS)
            requireDomain(
                pickupStartTime.isNotBlank() && pickupEndTime.isNotBlank(),
                ContractRequestErrorCode.INVALID_PICKUP_TIME,
            )
            requireDomain(
                pickupStartTime.trim() <= pickupEndTime.trim(),
                ContractRequestErrorCode.INVALID_PICKUP_TIME,
            )
            validateSchedule(
                contractType = contractType,
                pickupDateFrom = pickupDateFrom,
                pickupDateTo = pickupDateTo,
                deliveryDateFrom = deliveryDateFrom,
                deliveryDateTo = deliveryDateTo,
                recurringPickupCycle = recurringPickupCycle,
                recurringPickupDaysOfWeek = recurringPickupDaysOfWeek,
                recurringPickupDayOfMonth = recurringPickupDayOfMonth,
            )
            requireDomain(
                targetUnitPrice == null || targetUnitPrice >= BigDecimal.ZERO,
                ContractRequestErrorCode.INVALID_TARGET_UNIT_PRICE,
            )

            val normalizedItems = items.map {
                it.copy(productName = it.productName.trim())
            }
            val representativeItem = normalizedItems.first()

            return ContractRequest(
                id = id,
                type = type,
                requesterType = type.requesterType,
                requesterId = requesterId,
                approverType = type.approverType,
                approverId = approverId,
                productId = productId,
                pickupRegion = pickupRegion.trim(),
                pickupAddress = pickupAddress?.trim()?.takeIf { it.isNotBlank() },
                contractType = contractType,
                pickupDateFrom = pickupDateFrom,
                pickupDateTo = pickupDateTo,
                deliveryDateFrom = deliveryDateFrom,
                deliveryDateTo = deliveryDateTo,
                recurringPickupCycle = recurringPickupCycle,
                recurringPickupDaysOfWeek = recurringPickupDaysOfWeek.distinct(),
                recurringPickupDayOfMonth = recurringPickupDayOfMonth,
                monthlyVolume = monthlyVolume,
                productCategory = representativeItem.productCategory,
                productName = representativeItem.productName,
                boxSize = representativeItem.boxSize,
                pickupStartTime = pickupStartTime.trim(),
                pickupEndTime = pickupEndTime.trim(),
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainType = representativeItem.coldChainType,
                targetUnitPrice = targetUnitPrice,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                items = normalizedItems,
                status = status,
            )
        }

        fun restore(
            id: UUID,
            type: ContractRequestType,
            requesterType: ContractPartyType,
            requesterId: UUID,
            approverType: ContractPartyType,
            approverId: UUID?,
            productId: UUID?,
            pickupRegion: String,
            pickupAddress: String?,
            contractType: ContractRequestContractType = ContractRequestContractType.SINGLE,
            pickupDateFrom: LocalDate? = null,
            pickupDateTo: LocalDate? = null,
            deliveryDateFrom: LocalDate? = null,
            deliveryDateTo: LocalDate? = null,
            recurringPickupCycle: RecurringPickupCycle? = null,
            recurringPickupDaysOfWeek: List<DayOfWeek> = emptyList(),
            recurringPickupDayOfMonth: Int? = null,
            monthlyVolume: Int,
            productCategory: ProductCategory,
            productName: String,
            boxSize: BoxSize,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryRequired: Boolean,
            returnRequired: Boolean,
            coldChainType: ColdChainType,
            targetUnitPrice: BigDecimal?,
            memo: String?,
            items: List<ContractRequestItem>,
            status: ContractRequestStatus,
        ): ContractRequest {
            requireDomain(
                requesterType == type.requesterType && approverType == type.approverType,
                ContractRequestErrorCode.INVALID_CONTRACT_PARTY,
            )

            return ContractRequest(
                id = id,
                type = type,
                requesterType = requesterType,
                requesterId = requesterId,
                approverType = approverType,
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
                items = items.ifEmpty {
                    listOf(
                        ContractRequestItem.create(
                            id = id,
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
                    )
                },
                status = status,
            )
        }

        private fun validateSchedule(
            contractType: ContractRequestContractType,
            pickupDateFrom: LocalDate?,
            pickupDateTo: LocalDate?,
            deliveryDateFrom: LocalDate?,
            deliveryDateTo: LocalDate?,
            recurringPickupCycle: RecurringPickupCycle?,
            recurringPickupDaysOfWeek: List<DayOfWeek>,
            recurringPickupDayOfMonth: Int?,
        ) {
            requireDomain(
                pickupDateFrom == null || pickupDateTo == null || !pickupDateFrom.isAfter(pickupDateTo),
                ContractRequestErrorCode.INVALID_PICKUP_DATE_RANGE,
            )
            requireDomain(
                deliveryDateFrom == null || deliveryDateTo == null || !deliveryDateFrom.isAfter(deliveryDateTo),
                ContractRequestErrorCode.INVALID_DELIVERY_DATE_RANGE,
            )
            requireDomain(
                pickupDateFrom == null || deliveryDateTo == null || !deliveryDateTo.isBefore(pickupDateFrom),
                ContractRequestErrorCode.INVALID_DELIVERY_DATE_RANGE,
            )

            if (contractType == ContractRequestContractType.RECURRING) {
                requireDomain(recurringPickupCycle != null, ContractRequestErrorCode.INVALID_RECURRING_PICKUP_RULE)
                when (recurringPickupCycle) {
                    RecurringPickupCycle.WEEKLY -> requireDomain(
                        recurringPickupDaysOfWeek.isNotEmpty(),
                        ContractRequestErrorCode.INVALID_RECURRING_PICKUP_RULE,
                    )
                    RecurringPickupCycle.MONTHLY -> requireDomain(
                        recurringPickupDayOfMonth != null && recurringPickupDayOfMonth in 1..31,
                        ContractRequestErrorCode.INVALID_RECURRING_PICKUP_RULE,
                    )
                    null -> Unit
                }
            }
        }
    }
}
