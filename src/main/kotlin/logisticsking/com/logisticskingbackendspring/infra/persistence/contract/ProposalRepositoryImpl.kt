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
    private val proposalQueryRepository: ProposalQueryRepository,
) : ProposalRepository {

    override fun save(proposal: Proposal): Proposal {
        return proposalJpaRepository.save(ProposalJpaEntity.from(proposal)).toDomain()
    }

    override fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): Proposal? {
        return proposalJpaRepository.findByIdAndAgencyId(id, agencyId)?.toDomain()
    }

    override fun findByIdAndAgencyIdForUpdate(
        id: UUID,
        agencyId: UUID,
    ): Proposal? {
        return proposalQueryRepository.findByIdAndAgencyIdForUpdate(id, agencyId)?.toDomain()
    }

    override fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): Proposal? {
        return proposalJpaRepository.findByIdAndVendorId(id, vendorId)?.toDomain()
    }

    override fun findAllByContractRequestId(contractRequestId: UUID): List<Proposal> {
        return proposalJpaRepository.findAllByContractRequestIdOrderByCreatedAtDesc(contractRequestId)
            .map(ProposalJpaEntity::toDomain)
    }

    override fun findAllByContractRequestIdForUpdate(contractRequestId: UUID): List<Proposal> {
        return proposalQueryRepository.findAllByContractRequestIdForUpdate(contractRequestId)
            .map(ProposalJpaEntity::toDomain)
    }

    override fun findAllByContractRequestId(contractRequestId: UUID, pageable: Pageable): Page<Proposal> {
        return proposalJpaRepository.findAllByContractRequestIdOrderByCreatedAtDesc(contractRequestId, pageable)
            .map(ProposalJpaEntity::toDomain)
    }

    override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Proposal> {
        return proposalJpaRepository.findAllByAgencyIdOrderByCreatedAtDesc(agencyId, pageable)
            .map(ProposalJpaEntity::toDomain)
    }

    override fun saveAll(proposals: List<Proposal>): List<Proposal> {
        return proposalJpaRepository.saveAll(proposals.map(ProposalJpaEntity::from))
            .map(ProposalJpaEntity::toDomain)
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
}
