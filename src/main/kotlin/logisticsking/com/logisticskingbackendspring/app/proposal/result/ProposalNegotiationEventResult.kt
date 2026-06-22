package logisticsking.com.logisticskingbackendspring.app.proposal.result

import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEvent
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventItem
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventType
import java.math.BigDecimal
import java.util.UUID

data class ProposalNegotiationEventResult(
    val eventId: UUID,
    val proposalId: UUID,
    val sequence: Long,
    val actorType: ContractPartyType,
    val eventType: ProposalNegotiationEventType,
    val unitPrice: BigDecimal?,
    val items: List<ProposalNegotiationEventItemResult>,
    val memo: String?,
    val status: ProposalNegotiationEventStatus,
) {
    companion object {
        fun from(event: ProposalNegotiationEvent): ProposalNegotiationEventResult {
            return ProposalNegotiationEventResult(
                eventId = event.id,
                proposalId = event.proposalId,
                sequence = event.sequence,
                actorType = event.actorType,
                eventType = event.eventType,
                unitPrice = event.unitPrice,
                items = event.items.map(ProposalNegotiationEventItemResult::from),
                memo = event.memo,
                status = event.status,
            )
        }
    }
}

data class ProposalNegotiationEventItemResult(
    val itemId: UUID,
    val contractRequestItemId: UUID,
    val unitPrice: BigDecimal,
) {
    companion object {
        fun from(item: ProposalNegotiationEventItem): ProposalNegotiationEventItemResult {
            return ProposalNegotiationEventItemResult(
                itemId = item.id,
                contractRequestItemId = item.contractRequestItemId,
                unitPrice = item.unitPrice,
            )
        }
    }
}
