package logisticsking.com.logisticskingbackendspring.domain.driverwork

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface DriverWorkApplicationRepository {
    fun save(application: DriverWorkApplication): DriverWorkApplication
    fun saveAll(applications: List<DriverWorkApplication>): List<DriverWorkApplication>
    fun findByIdAndDriverWorkId(
        id: UUID,
        driverWorkId: UUID,
    ): DriverWorkApplication?
    fun findByDriverWorkIdAndDeliverId(
        driverWorkId: UUID,
        deliverId: UUID,
    ): DriverWorkApplication?
    fun findAllByDriverWorkId(driverWorkId: UUID): List<DriverWorkApplication>
    fun findAllByDriverWorkId(
        driverWorkId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplication>
    fun findAllByDeliverId(
        deliverId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplication>
    fun existsAppliedByDriverWorkIdAndDeliverId(
        driverWorkId: UUID,
        deliverId: UUID,
    ): Boolean
}
