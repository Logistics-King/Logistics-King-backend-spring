package logisticsking.com.logisticskingbackendspring.domain.delivercontract

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface DeliverContractRepository {
    fun save(deliverContract: DeliverContract): DeliverContract
    fun findByIdAndAgencyId(
        id: UUID,
        agencyId: UUID,
    ): DeliverContract?
    fun findByIdAndDeliverId(
        id: UUID,
        deliverId: UUID,
    ): DeliverContract?
    fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<DeliverContract>
    fun findAllByDeliverId(deliverId: UUID, pageable: Pageable): Page<DeliverContract>
    fun existsActiveByAgencyIdAndDeliverId(
        agencyId: UUID,
        deliverId: UUID,
    ): Boolean
}
