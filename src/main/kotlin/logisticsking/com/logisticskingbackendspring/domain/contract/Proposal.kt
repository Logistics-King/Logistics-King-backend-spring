package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.util.UUID

class Proposal private constructor(
    val id: UUID,

    val contractRequestId: UUID,

    val vendorId: UUID,

    val agencyId: UUID,

    val unitPrice: BigDecimal,

    val initialUnitPrice: BigDecimal,

    val finalUnitPrice: BigDecimal?,

    val pendingNegotiationId: UUID?,

    val nextSequence: Long,

    val pickupStartTime: String,

    val pickupEndTime: String,

    val saturdayDeliveryAvailable: Boolean,

    val returnAvailable: Boolean,

    val coldChainType: ColdChainType,

    val memo: String?,

    val items: List<ProposalItem>,

    val status: ProposalStatus,
) {

    fun update(
        unitPrice: BigDecimal,
        pickupStartTime: String,
        pickupEndTime: String,
        saturdayDeliveryAvailable: Boolean,
        returnAvailable: Boolean,
        coldChainType: ColdChainType,
        memo: String?,
        items: List<ProposalItem>,
    ): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED,
            ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_UPDATED,
        )
        requireDomain(
            pendingNegotiationId == null,
            ProposalErrorCode.PROPOSAL_HAS_PENDING_NEGOTIATION,
        )

        return create(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            initialUnitPrice = unitPrice,
            finalUnitPrice = null,
            pendingNegotiationId = null,
            nextSequence = nextSequence,
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

    fun withdraw(): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED,
            ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_WITHDRAWN,
        )
        requireDomain(
            pendingNegotiationId == null,
            ProposalErrorCode.PROPOSAL_HAS_PENDING_NEGOTIATION,
        )

        return restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            initialUnitPrice = initialUnitPrice,
            finalUnitPrice = finalUnitPrice,
            pendingNegotiationId = pendingNegotiationId,
            nextSequence = nextSequence,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainType = coldChainType,
            memo = memo,
            items = items,
            status = ProposalStatus.WITHDRAWN,
        )
    }

    fun accept(): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED || status == ProposalStatus.NEGOTIATING,
            ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_ACCEPTED,
        )
        requireDomain(
            pendingNegotiationId == null,
            ProposalErrorCode.PROPOSAL_HAS_PENDING_NEGOTIATION,
        )

        return changeStatus(ProposalStatus.ACCEPTED)
    }

    fun reject(): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED || status == ProposalStatus.NEGOTIATING,
            ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_REJECTED,
        )
        requireDomain(
            pendingNegotiationId == null,
            ProposalErrorCode.PROPOSAL_HAS_PENDING_NEGOTIATION,
        )

        return changeStatus(ProposalStatus.REJECTED)
    }

    fun startPriceNegotiation(
        eventId: UUID,
    ): Proposal {
        requireDomain(
            status == ProposalStatus.SUBMITTED || status == ProposalStatus.NEGOTIATING,
            ProposalErrorCode.PROPOSAL_CANNOT_BE_NEGOTIATED,
        )
        requireDomain(
            pendingNegotiationId == null,
            ProposalErrorCode.PROPOSAL_HAS_PENDING_NEGOTIATION,
        )
        return restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            initialUnitPrice = initialUnitPrice,
            finalUnitPrice = finalUnitPrice,
            pendingNegotiationId = eventId,
            nextSequence = nextSequence + 1,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainType = coldChainType,
            memo = memo,
            items = items,
            status = ProposalStatus.NEGOTIATING,
        )
    }

    fun acceptPendingNegotiation(
        pendingEventId: UUID,
        unitPrice: BigDecimal,
        items: List<ProposalNegotiationEventItem>,
    ): Proposal {
        requireDomain(
            status == ProposalStatus.NEGOTIATING,
            ProposalErrorCode.PROPOSAL_CANNOT_ACCEPT_NEGOTIATION,
        )
        requireDomain(
            pendingNegotiationId == pendingEventId,
            ProposalErrorCode.NEGOTIATION_EVENT_IS_NOT_PENDING,
        )
        requireDomain(unitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)

        return restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            initialUnitPrice = initialUnitPrice,
            finalUnitPrice = unitPrice,
            pendingNegotiationId = null,
            nextSequence = nextSequence + 1,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainType = coldChainType,
            memo = memo,
            items = applyNegotiationItems(items, unitPrice),
            status = ProposalStatus.NEGOTIATING,
        )
    }

    fun rejectPendingNegotiation(pendingEventId: UUID): Proposal {
        requireDomain(
            status == ProposalStatus.NEGOTIATING,
            ProposalErrorCode.PROPOSAL_CANNOT_REJECT_NEGOTIATION,
        )
        requireDomain(
            pendingNegotiationId == pendingEventId,
            ProposalErrorCode.NEGOTIATION_EVENT_IS_NOT_PENDING,
        )

        return restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            initialUnitPrice = initialUnitPrice,
            finalUnitPrice = finalUnitPrice,
            pendingNegotiationId = null,
            nextSequence = nextSequence + 1,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainType = coldChainType,
            memo = memo,
            items = items,
            status = ProposalStatus.NEGOTIATING,
        )
    }

    private fun changeStatus(status: ProposalStatus): Proposal {
        return restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            initialUnitPrice = initialUnitPrice,
            finalUnitPrice = finalUnitPrice,
            pendingNegotiationId = pendingNegotiationId,
            nextSequence = nextSequence,
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

    fun itemUnitPrice(contractRequestItemId: UUID): BigDecimal? {
        return items.firstOrNull { it.contractRequestItemId == contractRequestItemId }?.unitPrice
    }

    private fun applyNegotiationItems(
        negotiationItems: List<ProposalNegotiationEventItem>,
        fallbackUnitPrice: BigDecimal,
    ): List<ProposalItem> {
        if (negotiationItems.isEmpty()) {
            return items.map { it.changeUnitPrice(fallbackUnitPrice) }
        }

        val unitPriceByRequestItemId = negotiationItems.associateBy(
            keySelector = ProposalNegotiationEventItem::contractRequestItemId,
            valueTransform = ProposalNegotiationEventItem::unitPrice,
        )

        return items.map { item ->
            item.changeUnitPrice(unitPriceByRequestItemId[item.contractRequestItemId] ?: item.unitPrice)
        }
    }

    companion object {
        fun create(
            id: UUID,
            contractRequestId: UUID,
            vendorId: UUID,
            agencyId: UUID,
            unitPrice: BigDecimal,
            initialUnitPrice: BigDecimal = unitPrice,
            finalUnitPrice: BigDecimal? = null,
            pendingNegotiationId: UUID? = null,
            nextSequence: Long = 1,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            coldChainType: ColdChainType,
            memo: String?,
            items: List<ProposalItem>,
            status: ProposalStatus = ProposalStatus.SUBMITTED,
        ): Proposal {
            requireDomain(unitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)
            requireDomain(initialUnitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)
            requireDomain(finalUnitPrice == null || finalUnitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)
            requireDomain(nextSequence > 0, ProposalErrorCode.INVALID_NEGOTIATION_SEQUENCE)
            requireDomain(items.isNotEmpty(), ProposalErrorCode.INVALID_PROPOSAL_ITEMS)
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
                initialUnitPrice = initialUnitPrice,
                finalUnitPrice = finalUnitPrice,
                pendingNegotiationId = pendingNegotiationId,
                nextSequence = nextSequence,
                pickupStartTime = pickupStartTime.trim(),
                pickupEndTime = pickupEndTime.trim(),
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainType = coldChainType,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                items = items,
                status = status,
            )
        }

        fun restore(
            id: UUID,
            contractRequestId: UUID,
            vendorId: UUID,
            agencyId: UUID,
            unitPrice: BigDecimal,
            initialUnitPrice: BigDecimal,
            finalUnitPrice: BigDecimal?,
            pendingNegotiationId: UUID?,
            nextSequence: Long,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            coldChainType: ColdChainType,
            memo: String?,
            items: List<ProposalItem>,
            status: ProposalStatus,
        ): Proposal {
            return Proposal(
                id = id,
                contractRequestId = contractRequestId,
                vendorId = vendorId,
                agencyId = agencyId,
                unitPrice = unitPrice,
                initialUnitPrice = initialUnitPrice,
                finalUnitPrice = finalUnitPrice,
                pendingNegotiationId = pendingNegotiationId,
                nextSequence = nextSequence,
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
