package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.util.UUID

class ProposalNegotiationEvent private constructor(
    val id: UUID,
    val proposalId: UUID,
    val sequence: Long,
    val actorType: ContractPartyType,
    val eventType: ProposalNegotiationEventType,
    val unitPrice: BigDecimal?,
    val memo: String?,
    val status: ProposalNegotiationEventStatus,
) {

    fun accept(): ProposalNegotiationEvent {
        requireDomain(
            eventType == ProposalNegotiationEventType.PRICE_OFFER &&
                status == ProposalNegotiationEventStatus.PENDING,
            ProposalErrorCode.NEGOTIATION_EVENT_IS_NOT_PENDING,
        )

        return restore(
            id = id,
            proposalId = proposalId,
            sequence = sequence,
            actorType = actorType,
            eventType = eventType,
            unitPrice = unitPrice,
            memo = memo,
            status = ProposalNegotiationEventStatus.ACCEPTED,
        )
    }

    fun reject(): ProposalNegotiationEvent {
        requireDomain(
            eventType == ProposalNegotiationEventType.PRICE_OFFER &&
                status == ProposalNegotiationEventStatus.PENDING,
            ProposalErrorCode.NEGOTIATION_EVENT_IS_NOT_PENDING,
        )

        return restore(
            id = id,
            proposalId = proposalId,
            sequence = sequence,
            actorType = actorType,
            eventType = eventType,
            unitPrice = unitPrice,
            memo = memo,
            status = ProposalNegotiationEventStatus.REJECTED,
        )
    }

    companion object {
        fun priceOffer(
            id: UUID,
            proposalId: UUID,
            sequence: Long,
            actorType: ContractPartyType,
            unitPrice: BigDecimal,
            memo: String?,
        ): ProposalNegotiationEvent {
            requireDomain(unitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)

            return restore(
                id = id,
                proposalId = proposalId,
                sequence = sequence,
                actorType = actorType,
                eventType = ProposalNegotiationEventType.PRICE_OFFER,
                unitPrice = unitPrice,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                status = ProposalNegotiationEventStatus.PENDING,
            )
        }

        fun recorded(
            id: UUID,
            proposalId: UUID,
            sequence: Long,
            actorType: ContractPartyType,
            eventType: ProposalNegotiationEventType,
            memo: String?,
        ): ProposalNegotiationEvent {
            requireDomain(
                eventType != ProposalNegotiationEventType.PRICE_OFFER,
                ProposalErrorCode.INVALID_NEGOTIATION_EVENT_TYPE,
            )

            return restore(
                id = id,
                proposalId = proposalId,
                sequence = sequence,
                actorType = actorType,
                eventType = eventType,
                unitPrice = null,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                status = ProposalNegotiationEventStatus.RECORDED,
            )
        }

        fun restore(
            id: UUID,
            proposalId: UUID,
            sequence: Long,
            actorType: ContractPartyType,
            eventType: ProposalNegotiationEventType,
            unitPrice: BigDecimal?,
            memo: String?,
            status: ProposalNegotiationEventStatus,
        ): ProposalNegotiationEvent {
            requireDomain(sequence > 0, ProposalErrorCode.INVALID_NEGOTIATION_SEQUENCE)

            return ProposalNegotiationEvent(
                id = id,
                proposalId = proposalId,
                sequence = sequence,
                actorType = actorType,
                eventType = eventType,
                unitPrice = unitPrice,
                memo = memo,
                status = status,
            )
        }
    }
}
