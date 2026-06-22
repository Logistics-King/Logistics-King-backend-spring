package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProposalItemJpaRepository : JpaRepository<ProposalItemJpaEntity, UUID> {
    fun deleteAllByProposalId(proposalId: UUID)
    fun findAllByProposalIdOrderByCreatedAtAsc(proposalId: UUID): List<ProposalItemJpaEntity>
    fun findAllByProposalIdInOrderByCreatedAtAsc(proposalIds: Collection<UUID>): List<ProposalItemJpaEntity>
}
