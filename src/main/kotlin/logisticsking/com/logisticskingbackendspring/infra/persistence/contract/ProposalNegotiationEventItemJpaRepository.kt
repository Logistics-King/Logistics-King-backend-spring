package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProposalNegotiationEventItemJpaRepository : JpaRepository<ProposalNegotiationEventItemJpaEntity, UUID> {
    fun deleteAllByProposalNegotiationEventId(proposalNegotiationEventId: UUID)
    fun findAllByProposalNegotiationEventIdOrderByCreatedAtAsc(
        proposalNegotiationEventId: UUID,
    ): List<ProposalNegotiationEventItemJpaEntity>
    fun findAllByProposalNegotiationEventIdInOrderByCreatedAtAsc(
        proposalNegotiationEventIds: Collection<UUID>,
    ): List<ProposalNegotiationEventItemJpaEntity>
}
