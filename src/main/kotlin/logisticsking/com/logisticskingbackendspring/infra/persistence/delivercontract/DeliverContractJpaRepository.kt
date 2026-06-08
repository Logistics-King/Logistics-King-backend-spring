package logisticsking.com.logisticskingbackendspring.infra.persistence.delivercontract

import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DeliverContractJpaRepository : JpaRepository<DeliverContractJpaEntity, UUID> {
    fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): DeliverContractJpaEntity?

    fun findByIdAndDeliverId(
        id: UUID,
        deliverId: UUID,
    ): DeliverContractJpaEntity?

    fun findAllByAgencyIdOrderByCreatedAtDesc(agencyId: UUID): List<DeliverContractJpaEntity>
    fun findAllByDeliverIdOrderByCreatedAtDesc(deliverId: UUID): List<DeliverContractJpaEntity>
    fun existsByAgencyIdAndDeliverIdAndStatusIn(
        agencyId: UUID,
        deliverId: UUID,
        statuses: Collection<DeliverContractStatus>,
    ): Boolean
}
