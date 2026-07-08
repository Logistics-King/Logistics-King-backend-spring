package logisticsking.com.logisticskingbackendspring.infra.persistence.driverwork

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWork
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkStatus
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "driver_works")
class DriverWorkJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "agency_id", columnDefinition = "BINARY(16)", nullable = false)
    val agencyId: UUID,

    @Column(name = "contract_id", columnDefinition = "BINARY(16)", nullable = false)
    val contractId: UUID,

    @Column(name = "title", nullable = false, length = 100)
    val title: String,

    @Column(name = "service_region", nullable = false, length = 100)
    val serviceRegion: String,

    @Column(name = "pickup_start_date", nullable = false)
    val pickupStartDate: LocalDate,

    @Column(name = "pickup_end_date")
    val pickupEndDate: LocalDate?,

    @Column(name = "expected_volume", nullable = false)
    val expectedVolume: Int,

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    val unitPrice: BigDecimal,

    @Column(name = "assigned_deliver_id", columnDefinition = "BINARY(16)")
    val assignedDeliverId: UUID?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: DriverWorkStatus,

    @Column(name = "memo", length = 255)
    val memo: String?,
) : BaseJpaEntity() {

    fun toDomain(): DriverWork {
        return DriverWork.restore(
            id = id,
            agencyId = agencyId,
            contractId = contractId,
            title = title,
            serviceRegion = serviceRegion,
            pickupStartDate = pickupStartDate,
            pickupEndDate = pickupEndDate,
            expectedVolume = expectedVolume,
            unitPrice = unitPrice,
            assignedDeliverId = assignedDeliverId,
            status = status,
            memo = memo,
        )
    }

    companion object {
        fun from(driverWork: DriverWork): DriverWorkJpaEntity {
            return DriverWorkJpaEntity(
                id = driverWork.id,
                agencyId = driverWork.agencyId,
                contractId = driverWork.contractId,
                title = driverWork.title,
                serviceRegion = driverWork.serviceRegion,
                pickupStartDate = driverWork.pickupStartDate,
                pickupEndDate = driverWork.pickupEndDate,
                expectedVolume = driverWork.expectedVolume,
                unitPrice = driverWork.unitPrice,
                assignedDeliverId = driverWork.assignedDeliverId,
                status = driverWork.status,
                memo = driverWork.memo,
            )
        }
    }
}
