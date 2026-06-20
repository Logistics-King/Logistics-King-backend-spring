package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContractItemJpaRepository : JpaRepository<ContractItemJpaEntity, UUID> {
    fun findAllByContractIdOrderByCreatedAtAsc(contractId: UUID): List<ContractItemJpaEntity>
    fun findAllByContractIdInOrderByCreatedAtAsc(contractIds: Collection<UUID>): List<ContractItemJpaEntity>
    fun deleteAllByContractId(contractId: UUID)
}
