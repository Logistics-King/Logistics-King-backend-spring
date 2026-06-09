package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContractJpaRepository : JpaRepository<ContractJpaEntity, UUID> {
    fun findAllByVendorIdOrderByCreatedAtDesc(vendorId: UUID): List<ContractJpaEntity>
    fun findAllByAgencyIdOrderByCreatedAtDesc(agencyId: UUID): List<ContractJpaEntity>
    fun existsByContractRequestId(contractRequestId: UUID): Boolean
}
