package logisticsking.com.logisticskingbackendspring.infra.persistence.delivercontract

import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContract
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractRepository
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DeliverContractRepositoryImpl(
    private val jpaRepository: DeliverContractJpaRepository,
) : DeliverContractRepository {

    override fun save(deliverContract: DeliverContract): DeliverContract {
        return jpaRepository.save(DeliverContractJpaEntity.from(deliverContract)).toDomain()
    }

    override fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): DeliverContract? {
        return jpaRepository.findByIdAndAgencyId(id, agencyId)?.toDomain()
    }

    override fun findByIdAndDeliverId(
        id: UUID,
        deliverId: UUID,
    ): DeliverContract? {
        return jpaRepository.findByIdAndDeliverId(id, deliverId)?.toDomain()
    }

    override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<DeliverContract> {
        return jpaRepository.findAllByAgencyIdOrderByCreatedAtDesc(agencyId, pageable)
            .map(DeliverContractJpaEntity::toDomain)
    }

    override fun findAllByDeliverId(deliverId: UUID, pageable: Pageable): Page<DeliverContract> {
        return jpaRepository.findAllByDeliverIdOrderByCreatedAtDesc(deliverId, pageable)
            .map(DeliverContractJpaEntity::toDomain)
    }

    override fun existsActiveByAgencyIdAndDeliverId(
        agencyId: UUID,
        deliverId: UUID,
    ): Boolean {
        return jpaRepository.existsByAgencyIdAndDeliverIdAndStatusIn(
            agencyId = agencyId,
            deliverId = deliverId,
            statuses = ACTIVE_STATUSES,
        )
    }

    private companion object {
        private val ACTIVE_STATUSES = setOf(
            DeliverContractStatus.REQUESTED,
            DeliverContractStatus.ACCEPTED,
        )
    }
}
