package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ContractRequestJpaRepository : JpaRepository<ContractRequestJpaEntity, UUID> {
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): ContractRequestJpaEntity?

    fun findAllByVendorIdOrderByCreatedAtDesc(vendorId: UUID): List<ContractRequestJpaEntity>
}
