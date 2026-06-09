package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ProposalJpaRepository : JpaRepository<ProposalJpaEntity, UUID> {
    fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): ProposalJpaEntity?
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): ProposalJpaEntity?

    fun findAllByContractRequestIdOrderByCreatedAtDesc(contractRequestId: UUID): List<ProposalJpaEntity>
    fun findAllByContractRequestIdOrderByCreatedAtDesc(
        contractRequestId: UUID,
        pageable: Pageable,
    ): Page<ProposalJpaEntity>
    fun findAllByAgencyIdOrderByCreatedAtDesc(
        agencyId: UUID,
        pageable: Pageable,
    ): Page<ProposalJpaEntity>
    fun existsByContractRequestIdAndAgencyId(
        contractRequestId: UUID,
        agencyId: UUID,
    ): Boolean
}
