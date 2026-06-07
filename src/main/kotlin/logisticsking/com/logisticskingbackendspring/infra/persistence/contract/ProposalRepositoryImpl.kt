package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.Proposal
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ProposalRepositoryImpl(
    private val proposalJpaRepository: ProposalJpaRepository,
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

    override fun findAllByContractRequestId(contractRequestId: UUID): List<Proposal> {
        return proposalJpaRepository.findAllByContractRequestIdOrderByCreatedAtDesc(contractRequestId)
            .map(ProposalJpaEntity::toDomain)
    }

    override fun findAllByAgencyId(agencyId: UUID): List<Proposal> {
        return proposalJpaRepository.findAllByAgencyIdOrderByCreatedAtDesc(agencyId)
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
