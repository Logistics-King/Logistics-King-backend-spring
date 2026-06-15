package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContractRequestItemJpaRepository : JpaRepository<ContractRequestItemJpaEntity, UUID> {
    fun findAllByContractRequestIdOrderByCreatedAtAsc(contractRequestId: UUID): List<ContractRequestItemJpaEntity>
    fun findAllByContractRequestIdInOrderByCreatedAtAsc(contractRequestIds: Collection<UUID>): List<ContractRequestItemJpaEntity>
    fun deleteAllByContractRequestId(contractRequestId: UUID)
}
