package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContractRequestJpaRepository : JpaRepository<ContractRequestJpaEntity, UUID> {
    fun findAllByRequesterTypeAndRequesterIdOrderByCreatedAtDesc(
        requesterType: ContractPartyType,
        requesterId: UUID,
        pageable: Pageable,
    ): Page<ContractRequestJpaEntity>

    fun findAllByApproverTypeAndApproverIdOrderByCreatedAtDesc(
        approverType: ContractPartyType,
        approverId: UUID,
        pageable: Pageable,
    ): Page<ContractRequestJpaEntity>

    fun findAllByStatusOrderByCreatedAtDesc(
        status: ContractRequestStatus,
        pageable: Pageable,
    ): Page<ContractRequestJpaEntity>
}
