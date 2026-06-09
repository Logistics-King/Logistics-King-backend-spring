package logisticsking.com.logisticskingbackendspring.domain.contract

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ProposalRepository {
    fun save(proposal: Proposal): Proposal
    fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): Proposal?
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): Proposal?
    fun findAllByContractRequestId(contractRequestId: UUID): List<Proposal>
    fun findAllByContractRequestId(contractRequestId: UUID, pageable: Pageable): Page<Proposal>
    fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Proposal>
    fun saveAll(proposals: List<Proposal>): List<Proposal>
    fun existsByContractRequestIdAndAgencyId(
        contractRequestId: UUID,
        agencyId: UUID,
    ): Boolean
}
