package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.Proposal
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ProposalRepositoryImpl(
    private val proposalJpaRepository: ProposalJpaRepository,
    private val proposalItemJpaRepository: ProposalItemJpaRepository,
    private val proposalQueryRepository: ProposalQueryRepository,
) : ProposalRepository {

    override fun save(proposal: Proposal): Proposal {
        val saved = proposalJpaRepository.save(ProposalJpaEntity.from(proposal))
        proposalItemJpaRepository.deleteAllByProposalId(saved.id)
        proposalItemJpaRepository.saveAll(
            proposal.items.map { ProposalItemJpaEntity.from(saved.id, it) }
        )

        return saved.toDomain(proposalItemJpaRepository.findAllByProposalIdOrderByCreatedAtAsc(saved.id))
    }

    override fun findById(id: UUID): Proposal? {
        return proposalJpaRepository.findById(id).orElse(null)?.toDomainWithItems()
    }

    override fun findByIdForUpdate(id: UUID): Proposal? {
        return proposalQueryRepository.findByIdForUpdate(id)?.toDomainWithItems()
    }

    override fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): Proposal? {
        return proposalJpaRepository.findByIdAndAgencyId(id, agencyId)?.toDomainWithItems()
    }

    override fun findByIdAndAgencyIdForUpdate(
        id: UUID,
        agencyId: UUID,
    ): Proposal? {
        return proposalQueryRepository.findByIdAndAgencyIdForUpdate(id, agencyId)?.toDomainWithItems()
    }

    override fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): Proposal? {
        return proposalJpaRepository.findByIdAndVendorId(id, vendorId)?.toDomainWithItems()
    }

    override fun findAllByContractRequestId(contractRequestId: UUID): List<Proposal> {
        return proposalJpaRepository.findAllByContractRequestIdOrderByCreatedAtDesc(contractRequestId)
            .toDomainListWithItems()
    }

    override fun findAllByContractRequestIdForUpdate(contractRequestId: UUID): List<Proposal> {
        return proposalQueryRepository.findAllByContractRequestIdForUpdate(contractRequestId)
            .toDomainListWithItems()
    }

    override fun findAllByContractRequestId(contractRequestId: UUID, pageable: Pageable): Page<Proposal> {
        return proposalJpaRepository.findAllByContractRequestIdOrderByCreatedAtDesc(contractRequestId, pageable)
            .toDomainPageWithItems()
    }

    override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Proposal> {
        return proposalJpaRepository.findAllByAgencyIdOrderByCreatedAtDesc(agencyId, pageable)
            .toDomainPageWithItems()
    }

    override fun saveAll(proposals: List<Proposal>): List<Proposal> {
        return proposals.map(::save)
    }

    override fun existsByContractRequestIdAndAgencyId(
        contractRequestId: UUID,
        agencyId: UUID,
    ): Boolean {
        return proposalJpaRepository.existsByContractRequestIdAndAgencyId(
            contractRequestId = contractRequestId,
            agencyId = agencyId,
        )
    }

    private fun ProposalJpaEntity.toDomainWithItems(): Proposal {
        return toDomain(proposalItemJpaRepository.findAllByProposalIdOrderByCreatedAtAsc(id))
    }

    private fun List<ProposalJpaEntity>.toDomainListWithItems(): List<Proposal> {
        val itemsByProposalId = proposalItemsByProposalId(map(ProposalJpaEntity::id))
        return map { it.toDomain(itemsByProposalId[it.id].orEmpty()) }
    }

    private fun Page<ProposalJpaEntity>.toDomainPageWithItems(): Page<Proposal> {
        val itemsByProposalId = proposalItemsByProposalId(content.map(ProposalJpaEntity::id))
        return map { it.toDomain(itemsByProposalId[it.id].orEmpty()) }
    }

    private fun proposalItemsByProposalId(proposalIds: Collection<UUID>): Map<UUID, List<ProposalItemJpaEntity>> {
        if (proposalIds.isEmpty()) {
            return emptyMap()
        }

        return proposalItemJpaRepository.findAllByProposalIdInOrderByCreatedAtAsc(proposalIds)
            .groupBy(ProposalItemJpaEntity::proposalId)
    }
}
