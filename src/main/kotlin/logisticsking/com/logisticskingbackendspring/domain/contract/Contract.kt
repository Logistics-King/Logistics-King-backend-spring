package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

class Contract private constructor(
    val id: UUID,
    val contractRequestId: UUID,
    val proposalId: UUID,
    val vendorId: UUID,
    val agencyId: UUID,
    val pickupRegion: String,
    val pickupAddress: String?,
    val monthlyVolume: Int,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: String,
    val unitPrice: BigDecimal,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
    val status: ContractStatus,
) {
    companion object {
        fun create(
            id: UUID,
            contractRequest: ContractRequest,
            proposal: Proposal,
        ): Contract {
            requireDomain(
                contractRequest.id == proposal.contractRequestId &&
                    contractRequest.vendorId == proposal.vendorId,
                ContractErrorCode.INVALID_CONTRACT_REQUEST_PROPOSAL,
            )

            return Contract(
                id = id,
                contractRequestId = contractRequest.id,
                proposalId = proposal.id,
                vendorId = contractRequest.vendorId,
                agencyId = proposal.agencyId,
                pickupRegion = contractRequest.pickupRegion,
                pickupAddress = contractRequest.pickupAddress,
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
            monthlyVolume: Int,
            productCategory: ProductCategory,
            productName: String,
            boxSize: String,
            unitPrice: BigDecimal,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            coldChainType: ColdChainType,
            memo: String?,
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
                status = status,
            )
        }
    }
}
