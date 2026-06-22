package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEvent
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ProposalNegotiationEventRepositoryImpl(
    private val proposalNegotiationEventJpaRepository: ProposalNegotiationEventJpaRepository,
    private val proposalNegotiationEventItemJpaRepository: ProposalNegotiationEventItemJpaRepository,
) : ProposalNegotiationEventRepository {

    override fun save(event: ProposalNegotiationEvent): ProposalNegotiationEvent {
        val saved = proposalNegotiationEventJpaRepository.save(ProposalNegotiationEventJpaEntity.from(event))
        proposalNegotiationEventItemJpaRepository.deleteAllByProposalNegotiationEventId(saved.id)
        proposalNegotiationEventItemJpaRepository.saveAll(
            event.items.map { ProposalNegotiationEventItemJpaEntity.from(saved.id, it) }
        )

        return saved.toDomain(
            proposalNegotiationEventItemJpaRepository.findAllByProposalNegotiationEventIdOrderByCreatedAtAsc(saved.id)
        )
    }

    override fun findByIdAndProposalId(
        id: UUID,
        proposalId: UUID,
    ): ProposalNegotiationEvent? {
        return proposalNegotiationEventJpaRepository.findByIdAndProposalId(id, proposalId)?.toDomainWithItems()
    }

    override fun findAllByProposalId(proposalId: UUID): List<ProposalNegotiationEvent> {
        val events = proposalNegotiationEventJpaRepository.findAllByProposalIdOrderBySequenceAsc(proposalId)
        val itemsByEventId = proposalNegotiationEventItemJpaRepository
            .findAllByProposalNegotiationEventIdInOrderByCreatedAtAsc(events.map(ProposalNegotiationEventJpaEntity::id))
            .groupBy(ProposalNegotiationEventItemJpaEntity::proposalNegotiationEventId)

        return events.map { event -> event.toDomain(itemsByEventId[event.id].orEmpty()) }
    }

    private fun ProposalNegotiationEventJpaEntity.toDomainWithItems(): ProposalNegotiationEvent {
        return toDomain(
            proposalNegotiationEventItemJpaRepository.findAllByProposalNegotiationEventIdOrderByCreatedAtAsc(id)
        )
    }
}
