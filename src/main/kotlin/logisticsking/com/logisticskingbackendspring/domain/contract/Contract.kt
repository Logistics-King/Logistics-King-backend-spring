package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID

class Contract private constructor(
    val id: UUID,
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
    val items: List<ContractItem>,
    val status: ContractStatus,
) {
    companion object {
        fun create(
            id: UUID,
            contractRequest: ContractRequest,
            proposal: Proposal,
            items: List<ContractItem>,
        ): Contract {
            requireDomain(
                contractRequest.id == proposal.contractRequestId &&
                    contractRequest.vendorId == proposal.vendorId,
                ContractErrorCode.INVALID_CONTRACT_REQUEST_PROPOSAL,
            )
            requireDomain(items.isNotEmpty(), ContractRequestErrorCode.INVALID_ITEMS)

            return Contract(
                id = id,
                contractRequestId = contractRequest.id,
                proposalId = proposal.id,
                vendorId = contractRequest.vendorId,
                agencyId = proposal.agencyId,
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
                unitPrice = proposal.unitPrice,
                pickupStartTime = proposal.pickupStartTime,
                pickupEndTime = proposal.pickupEndTime,
                saturdayDeliveryAvailable = proposal.saturdayDeliveryAvailable,
                returnAvailable = proposal.returnAvailable,
                coldChainType = proposal.coldChainType,
                memo = proposal.memo,
                items = items,
                status = ContractStatus.ACTIVE,
            )
        }

        fun restore(
            id: UUID,
            contractRequestId: UUID,
            proposalId: UUID,
            vendorId: UUID,
            agencyId: UUID,
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
            unitPrice: BigDecimal,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            coldChainType: ColdChainType,
            memo: String?,
            items: List<ContractItem>,
            status: ContractStatus,
        ): Contract {
            return Contract(
                id = id,
                contractRequestId = contractRequestId,
                proposalId = proposalId,
                vendorId = vendorId,
                agencyId = agencyId,
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
                unitPrice = unitPrice,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainType = coldChainType,
                memo = memo,
                items = items,
                status = status,
            )
        }
    }
}
