package logisticsking.com.logisticskingbackendspring.domain.driverwork

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface DriverWorkRepository {
    fun save(driverWork: DriverWork): DriverWork
    fun findById(id: UUID): DriverWork?
    fun findByIdAndAgencyId(id: UUID, agencyId: UUID): DriverWork?
    fun findAllByAgencyId(
        agencyId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWork>
    fun findOpenByAgencyId(
        agencyId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWork>
    fun findAssignedByDeliverId(
        deliverId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWork>
}
