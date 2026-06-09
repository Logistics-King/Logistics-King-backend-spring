package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.util.UUID

class Proposal private constructor(
    val id: UUID,
    val contractRequestId: UUID,
    val vendorId: UUID,
    val agencyId: UUID,
    val unitPrice: BigDecimal,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainAvailable: Boolean,
    val memo: String?,
    val status: ProposalStatus,
) {

    fun update(
        unitPrice: BigDecimal,
        pickupStartTime: String,
        pickupEndTime: String,
        saturdayDeliveryAvailable: Boolean,
        returnAvailable: Boolean,
        coldChainAvailable: Boolean,
        memo: String?,
    ): Proposal {
        requireDomain(
            status != ProposalStatus.WITHDRAWN,
            ProposalErrorCode.WITHDRAWN_PROPOSAL_CANNOT_BE_UPDATED,
        )

        return create(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainAvailable = coldChainAvailable,
            memo = memo,
            status = status,
        )
    }

    fun withdraw(): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED,
            ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_WITHDRAWN,
        )

        return restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainAvailable = coldChainAvailable,
            memo = memo,
            status = ProposalStatus.WITHDRAWN,
        )
    }

    fun accept(): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED,
            ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_ACCEPTED,
        )

        return changeStatus(ProposalStatus.ACCEPTED)
    }

    fun reject(): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED,
            ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_REJECTED,
        )

        return changeStatus(ProposalStatus.REJECTED)
    }

    private fun changeStatus(status: ProposalStatus): Proposal {
        return restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainAvailable = coldChainAvailable,
            memo = memo,
            status = status,
        )
    }

    companion object {
        fun create(
            id: UUID,
            contractRequestId: UUID,
            vendorId: UUID,
            agencyId: UUID,
            unitPrice: BigDecimal,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            coldChainAvailable: Boolean,
            memo: String?,
            status: ProposalStatus = ProposalStatus.SUBMITTED,
        ): Proposal {
            requireDomain(unitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)
            requireDomain(
                pickupStartTime.isNotBlank() && pickupEndTime.isNotBlank(),
                ProposalErrorCode.INVALID_PICKUP_TIME,
            )

            return Proposal(
                id = id,
                contractRequestId = contractRequestId,
                vendorId = vendorId,
                agencyId = agencyId,
                unitPrice = unitPrice,
                pickupStartTime = pickupStartTime.trim(),
                pickupEndTime = pickupEndTime.trim(),
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainAvailable = coldChainAvailable,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                status = status,
            )
        }

        fun restore(
            id: UUID,
            contractRequestId: UUID,
            vendorId: UUID,
            agencyId: UUID,
            unitPrice: BigDecimal,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            coldChainAvailable: Boolean,
            memo: String?,
            status: ProposalStatus,
        ): Proposal {
            return Proposal(
                id = id,
                contractRequestId = contractRequestId,
                vendorId = vendorId,
                agencyId = agencyId,
                unitPrice = unitPrice,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainAvailable = coldChainAvailable,
                memo = memo,
                status = status,
            )
        }
    }
}
