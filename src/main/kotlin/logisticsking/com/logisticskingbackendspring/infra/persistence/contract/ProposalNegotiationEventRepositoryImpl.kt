package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEvent
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ProposalNegotiationEventRepositoryImpl(
    private val proposalNegotiationEventJpaRepository: ProposalNegotiationEventJpaRepository,
) : ProposalNegotiationEventRepository {

    override fun save(event: ProposalNegotiationEvent): ProposalNegotiationEvent {
        return proposalNegotiationEventJpaRepository.save(ProposalNegotiationEventJpaEntity.from(event)).toDomain()
    }

    override fun findByIdAndProposalId(
        id: UUID,
        proposalId: UUID,
    ): ProposalNegotiationEvent? {
        return proposalNegotiationEventJpaRepository.findByIdAndProposalId(id, proposalId)?.toDomain()
    }

    override fun findAllByProposalId(proposalId: UUID): List<ProposalNegotiationEvent> {
        return proposalNegotiationEventJpaRepository.findAllByProposalIdOrderBySequenceAsc(proposalId)
            .map(ProposalNegotiationEventJpaEntity::toDomain)
    }
}
