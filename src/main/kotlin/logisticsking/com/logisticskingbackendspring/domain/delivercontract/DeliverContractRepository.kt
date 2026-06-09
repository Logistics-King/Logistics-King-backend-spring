package logisticsking.com.logisticskingbackendspring.domain.delivercontract

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
    fun findAllByAgencyId(agencyId: UUID): List<DeliverContract>
    fun findAllByDeliverId(deliverId: UUID): List<DeliverContract>
    fun existsActiveByAgencyIdAndDeliverId(
        agencyId: UUID,
        deliverId: UUID,
    ): Boolean
}
