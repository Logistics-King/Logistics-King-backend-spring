package logisticsking.com.logisticskingbackendspring.infra.persistence.driverwork

import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplicationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DriverWorkApplicationJpaRepository : JpaRepository<DriverWorkApplicationJpaEntity, UUID> {
    fun findByIdAndDriverWorkId(id: UUID, driverWorkId: UUID): DriverWorkApplicationJpaEntity?
    fun findByDriverWorkIdAndDeliverId(driverWorkId: UUID, deliverId: UUID): DriverWorkApplicationJpaEntity?
    fun findAllByDriverWorkId(driverWorkId: UUID): List<DriverWorkApplicationJpaEntity>
    fun findAllByDriverWorkId(driverWorkId: UUID, pageable: Pageable): Page<DriverWorkApplicationJpaEntity>
    fun findAllByDeliverIdOrderByCreatedAtDesc(deliverId: UUID, pageable: Pageable): Page<DriverWorkApplicationJpaEntity>
    fun existsByDriverWorkIdAndDeliverIdAndStatus(
        driverWorkId: UUID,
        deliverId: UUID,
        status: DriverWorkApplicationStatus,
    ): Boolean
}
