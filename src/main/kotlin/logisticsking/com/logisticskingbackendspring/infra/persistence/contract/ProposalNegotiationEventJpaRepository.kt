package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProposalNegotiationEventJpaRepository : JpaRepository<ProposalNegotiationEventJpaEntity, UUID> {
    fun findByIdAndProposalId(
        id: UUID,
        proposalId: UUID,
    ): ProposalNegotiationEventJpaEntity?

    fun findAllByProposalIdOrderBySequenceAsc(proposalId: UUID): List<ProposalNegotiationEventJpaEntity>
}
