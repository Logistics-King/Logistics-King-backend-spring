package logisticsking.com.logisticskingbackendspring.infra.persistence.driverwork

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DriverWorkJpaRepository : JpaRepository<DriverWorkJpaEntity, UUID> {
    fun findByIdAndAgencyId(id: UUID, agencyId: UUID): DriverWorkJpaEntity?
}
