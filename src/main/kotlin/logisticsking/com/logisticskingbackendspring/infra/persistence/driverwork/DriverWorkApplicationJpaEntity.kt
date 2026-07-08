package logisticsking.com.logisticskingbackendspring.infra.persistence.driverwork

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplication
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplicationStatus
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.util.UUID

@Entity
@Table(name = "driver_work_applications")
class DriverWorkApplicationJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "driver_work_id", columnDefinition = "BINARY(16)", nullable = false)
    val driverWorkId: UUID,

    @Column(name = "deliver_id", columnDefinition = "BINARY(16)", nullable = false)
    val deliverId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: DriverWorkApplicationStatus,

    @Column(name = "memo", length = 255)
    val memo: String?,
) : BaseJpaEntity() {

    fun toDomain(): DriverWorkApplication {
        return DriverWorkApplication.restore(
            id = id,
            driverWorkId = driverWorkId,
            deliverId = deliverId,
            status = status,
            memo = memo,
        )
    }

    companion object {
        fun from(application: DriverWorkApplication): DriverWorkApplicationJpaEntity {
            return DriverWorkApplicationJpaEntity(
                id = application.id,
                driverWorkId = application.driverWorkId,
                deliverId = application.deliverId,
                status = application.status,
                memo = application.memo,
            )
        }
    }
}
