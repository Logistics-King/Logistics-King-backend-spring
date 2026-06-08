package logisticsking.com.logisticskingbackendspring.domain.contract

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
    fun findAllByAgencyId(agencyId: UUID): List<Proposal>
    fun saveAll(proposals: List<Proposal>): List<Proposal>
    fun existsByContractRequestIdAndAgencyId(
        contractRequestId: UUID,
        agencyId: UUID,
    ): Boolean
}
