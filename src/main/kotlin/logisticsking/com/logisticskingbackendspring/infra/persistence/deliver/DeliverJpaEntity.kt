package logisticsking.com.logisticskingbackendspring.infra.persistence.deliver

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.SoftDeletableJpaEntity
import java.util.UUID

@Entity
@Table(name = "delivers")
class DeliverJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    val userId: UUID,

    @Column(name = "agency_id", columnDefinition = "BINARY(16)", nullable = false)
    val agencyId: UUID,

    @Column(name = "driver_name", nullable = false, length = 50)
    val driverName: String,

    @Column(name = "phone_number", nullable = false, length = 30)
    val phoneNumber: String,

    @Column(name = "vehicle_number", length = 30)
    val vehicleNumber: String?,

    @Column(name = "service_regions", nullable = false, length = 500)
    val serviceRegions: String,

    @Column(name = "active", nullable = false)
    val active: Boolean,

    @Column(name = "memo", length = 255)
    val memo: String?,
) : SoftDeletableJpaEntity() {

    fun toDomain(): Deliver {
        return Deliver.restore(
            id = id,
            userId = userId,
            agencyId = agencyId,
            driverName = driverName,
            phoneNumber = phoneNumber,
            vehicleNumber = vehicleNumber,
            serviceRegions = serviceRegions.split(SERVICE_REGION_DELIMITER)
                .map(String::trim)
                .filter(String::isNotBlank),
            active = active,
            memo = memo,
        )
    }

    companion object {
        private const val SERVICE_REGION_DELIMITER = "|"

        fun from(deliver: Deliver): DeliverJpaEntity {
            return DeliverJpaEntity(
                id = deliver.id,
                userId = deliver.userId,
                agencyId = deliver.agencyId,
                driverName = deliver.driverName,
                phoneNumber = deliver.phoneNumber,
                vehicleNumber = deliver.vehicleNumber,
                serviceRegions = deliver.serviceRegions.joinToString(SERVICE_REGION_DELIMITER),
                active = deliver.active,
                memo = deliver.memo,
            )
        }
    }
}
