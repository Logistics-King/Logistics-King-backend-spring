package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContractJpaRepository : JpaRepository<ContractJpaEntity, UUID> {
    fun findAllByVendorIdOrderByCreatedAtDesc(vendorId: UUID, pageable: Pageable): Page<ContractJpaEntity>
    fun findAllByAgencyIdOrderByCreatedAtDesc(agencyId: UUID, pageable: Pageable): Page<ContractJpaEntity>
    fun existsByContractRequestId(contractRequestId: UUID): Boolean
}
