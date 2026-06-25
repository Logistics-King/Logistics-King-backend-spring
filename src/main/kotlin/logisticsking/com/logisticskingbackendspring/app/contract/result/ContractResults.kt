package logisticsking.com.logisticskingbackendspring.app.contract.result

import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestContractType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractItem
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.RecurringPickupCycle
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

data class ContractResult(
    val contractId: UUID,
    val contractRequestId: UUID,
    val proposalId: UUID,
    val vendorId: UUID,
    val agencyId: UUID,
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
    val unitPrice: BigDecimal,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
    val items: List<ContractItemResult>,
    val status: ContractStatus,
    val vendor: VendorResult?,
    val agency: AgencyResult?,
) {
    companion object {
        fun from(
            contract: Contract,
            vendor: Vendor? = null,
            agency: Agency? = null,
        ): ContractResult {
            return ContractResult(
                contractId = contract.id,
                contractRequestId = contract.contractRequestId,
                proposalId = contract.proposalId,
                vendorId = contract.vendorId,
                agencyId = contract.agencyId,
                pickupRegion = contract.pickupRegion,
                pickupAddress = contract.pickupAddress,
                contractType = contract.contractType,
                pickupDateFrom = contract.pickupDateFrom,
                pickupDateTo = contract.pickupDateTo,
                deliveryDateFrom = contract.deliveryDateFrom,
                deliveryDateTo = contract.deliveryDateTo,
                recurringPickupCycle = contract.recurringPickupCycle,
                recurringPickupDaysOfWeek = contract.recurringPickupDaysOfWeek,
                recurringPickupDayOfMonth = contract.recurringPickupDayOfMonth,
                monthlyVolume = contract.monthlyVolume,
                productCategory = contract.productCategory,
                productName = contract.productName,
                boxSize = contract.boxSize,
                unitPrice = contract.unitPrice,
                pickupStartTime = contract.pickupStartTime,
                pickupEndTime = contract.pickupEndTime,
                saturdayDeliveryAvailable = contract.saturdayDeliveryAvailable,
                returnAvailable = contract.returnAvailable,
                coldChainType = contract.coldChainType,
                memo = contract.memo,
                items = contract.items.map(ContractItemResult::from),
                status = contract.status,
                vendor = vendor?.let(VendorResult::from),
                agency = agency?.let(AgencyResult::from),
            )
        }
    }
}

data class ContractItemResult(
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
    val unitPrice: BigDecimal,
) {
    companion object {
        fun from(item: ContractItem): ContractItemResult {
            return ContractItemResult(
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
                unitPrice = item.unitPrice,
            )
        }
    }
}
