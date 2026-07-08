package logisticsking.com.logisticskingbackendspring.infra.persistence.driverwork

import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplication
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplicationRepository
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplicationStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DriverWorkApplicationRepositoryImpl(
    private val jpaRepository: DriverWorkApplicationJpaRepository,
) : DriverWorkApplicationRepository {

    override fun save(application: DriverWorkApplication): DriverWorkApplication {
        return jpaRepository.save(DriverWorkApplicationJpaEntity.from(application)).toDomain()
    }

    override fun saveAll(applications: List<DriverWorkApplication>): List<DriverWorkApplication> {
        return jpaRepository.saveAll(applications.map(DriverWorkApplicationJpaEntity::from))
            .map(DriverWorkApplicationJpaEntity::toDomain)
    }

    override fun findByIdAndDriverWorkId(
        id: UUID,
        driverWorkId: UUID,
    ): DriverWorkApplication? {
        return jpaRepository.findByIdAndDriverWorkId(id, driverWorkId)?.toDomain()
    }

    override fun findByDriverWorkIdAndDeliverId(
        driverWorkId: UUID,
        deliverId: UUID,
    ): DriverWorkApplication? {
        return jpaRepository.findByDriverWorkIdAndDeliverId(driverWorkId, deliverId)?.toDomain()
    }

    override fun findAllByDriverWorkId(driverWorkId: UUID): List<DriverWorkApplication> {
        return jpaRepository.findAllByDriverWorkId(driverWorkId)
            .map(DriverWorkApplicationJpaEntity::toDomain)
    }

    override fun findAllByDriverWorkId(
        driverWorkId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplication> {
        return jpaRepository.findAllByDriverWorkId(driverWorkId, pageable)
            .map(DriverWorkApplicationJpaEntity::toDomain)
    }

    override fun findAllByDeliverId(
        deliverId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplication> {
        return jpaRepository.findAllByDeliverIdOrderByCreatedAtDesc(deliverId, pageable)
            .map(DriverWorkApplicationJpaEntity::toDomain)
    }

    override fun existsAppliedByDriverWorkIdAndDeliverId(
        driverWorkId: UUID,
        deliverId: UUID,
    ): Boolean {
        return jpaRepository.existsByDriverWorkIdAndDeliverIdAndStatus(
            driverWorkId = driverWorkId,
            deliverId = deliverId,
            status = DriverWorkApplicationStatus.APPLIED,
        )
    }
}
