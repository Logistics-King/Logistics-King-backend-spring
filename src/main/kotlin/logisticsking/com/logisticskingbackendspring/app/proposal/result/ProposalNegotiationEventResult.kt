package logisticsking.com.logisticskingbackendspring.app.proposal.result

import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEvent
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
                memo = event.memo,
                status = event.status,
            )
        }
    }
}
