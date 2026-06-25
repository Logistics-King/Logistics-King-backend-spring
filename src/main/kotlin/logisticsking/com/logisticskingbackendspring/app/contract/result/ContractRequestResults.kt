package logisticsking.com.logisticskingbackendspring.app.contract.result

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestContractType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestItem
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestType
import logisticsking.com.logisticskingbackendspring.domain.contract.RecurringPickupCycle
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

data class ContractRequestResult(
    val contractRequestId: UUID,
    val vendorId: UUID,
    val agencyId: UUID?,
    val type: ContractRequestType,
    val requesterType: ContractPartyType,
    val requesterId: UUID,
    val approverType: ContractPartyType,
    val approverId: UUID?,
    val productId: UUID?,
    val pickupRegion: String,
    val pickupAddress: String?,
    val contractType: ContractRequestContractType,
    val pickupDateFrom: LocalDate?,
    val pickupDateTo: LocalDate?,
    val deliveryDateFrom: LocalDate?,
    val deliveryDateTo: LocalDate?,
    val recurringPickupCycle: RecurringPickupCycle?,
    val recurringPickupDaysOfWeek: List<DayOfWeek>,
    val recurringPickupDayOfMonth: Int?,
    val monthlyVolume: Int,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: BoxSize,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryRequired: Boolean,
    val returnRequired: Boolean,
    val coldChainType: ColdChainType,
    val targetUnitPrice: BigDecimal?,
    val memo: String?,
    val items: List<ContractRequestItemResult>,
    val status: ContractRequestStatus,
) {
    companion object {
        fun from(contractRequest: ContractRequest): ContractRequestResult {
            return ContractRequestResult(
                contractRequestId = contractRequest.id,
                vendorId = contractRequest.vendorId,
                agencyId = contractRequest.agencyId,
                type = contractRequest.type,
                requesterType = contractRequest.requesterType,
                requesterId = contractRequest.requesterId,
                approverType = contractRequest.approverType,
                approverId = contractRequest.approverId,
                productId = contractRequest.productId,
                pickupRegion = contractRequest.pickupRegion,
                pickupAddress = contractRequest.pickupAddress,
                contractType = contractRequest.contractType,
                pickupDateFrom = contractRequest.pickupDateFrom,
                pickupDateTo = contractRequest.pickupDateTo,
                deliveryDateFrom = contractRequest.deliveryDateFrom,
                deliveryDateTo = contractRequest.deliveryDateTo,
                recurringPickupCycle = contractRequest.recurringPickupCycle,
                recurringPickupDaysOfWeek = contractRequest.recurringPickupDaysOfWeek,
                recurringPickupDayOfMonth = contractRequest.recurringPickupDayOfMonth,
                monthlyVolume = contractRequest.monthlyVolume,
                productCategory = contractRequest.productCategory,
                productName = contractRequest.productName,
                boxSize = contractRequest.boxSize,
                pickupStartTime = contractRequest.pickupStartTime,
                pickupEndTime = contractRequest.pickupEndTime,
                saturdayDeliveryRequired = contractRequest.saturdayDeliveryRequired,
                returnRequired = contractRequest.returnRequired,
                coldChainType = contractRequest.coldChainType,
                targetUnitPrice = contractRequest.targetUnitPrice,
                memo = contractRequest.memo,
                items = contractRequest.items.map(ContractRequestItemResult::from),
                status = contractRequest.status,
            )
        }
    }
}

data class ContractRequestItemResult(
    val itemId: UUID,
    val productId: UUID?,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: BoxSize,
    val boxQuantity: Int,
    val itemQuantity: Int,
    val averageWeightGram: Int?,
    val fragile: Boolean,
    val liquid: Boolean,
    val freshFood: Boolean,
    val coldChainType: ColdChainType,
    val targetUnitPrice: BigDecimal?,
) {
    companion object {
        fun from(item: ContractRequestItem): ContractRequestItemResult {
            return ContractRequestItemResult(
                itemId = item.id,
                productId = item.productId,
                productCategory = item.productCategory,
                productName = item.productName,
                boxSize = item.boxSize,
                boxQuantity = item.boxQuantity,
                itemQuantity = item.itemQuantity,
                averageWeightGram = item.averageWeightGram,
                fragile = item.fragile,
                liquid = item.liquid,
                freshFood = item.freshFood,
                coldChainType = item.coldChainType,
                targetUnitPrice = item.targetUnitPrice,
            )
        }
    }
}
