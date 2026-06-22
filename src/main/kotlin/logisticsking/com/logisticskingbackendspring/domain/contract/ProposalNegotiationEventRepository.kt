package logisticsking.com.logisticskingbackendspring.domain.contract

import java.util.UUID

interface ProposalNegotiationEventRepository {
    fun save(event: ProposalNegotiationEvent): ProposalNegotiationEvent
    fun findByIdAndProposalId(
        id: UUID,
        proposalId: UUID,
    ): ProposalNegotiationEvent?
    fun findAllByProposalId(proposalId: UUID): List<ProposalNegotiationEvent>
}
