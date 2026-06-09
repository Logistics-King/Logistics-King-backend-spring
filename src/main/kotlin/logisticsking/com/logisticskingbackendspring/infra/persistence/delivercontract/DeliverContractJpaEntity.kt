package logisticsking.com.logisticskingbackendspring.infra.persistence.delivercontract

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContract
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractStatus
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "deliver_contracts")
class DeliverContractJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "agency_id", columnDefinition = "BINARY(16)", nullable = false)
    val agencyId: UUID,

    @Column(name = "deliver_id", columnDefinition = "BINARY(16)", nullable = false)
    val deliverId: UUID,

    @Column(name = "service_region", nullable = false, length = 100)
    val serviceRegion: String,

    @Column(name = "expected_monthly_volume", nullable = false)
    val expectedMonthlyVolume: Int,

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    val unitPrice: BigDecimal,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "end_date")
    val endDate: LocalDate?,

    @Column(name = "memo", length = 255)
    val memo: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: DeliverContractStatus,
) : BaseJpaEntity() {

    fun toDomain(): DeliverContract {
        return DeliverContract.restore(
            id = id,
            agencyId = agencyId,
            deliverId = deliverId,
            serviceRegion = serviceRegion,
            expectedMonthlyVolume = expectedMonthlyVolume,
            unitPrice = unitPrice,
            startDate = startDate,
            endDate = endDate,
            memo = memo,
            status = status,
        )
    }

    companion object {
        fun from(deliverContract: DeliverContract): DeliverContractJpaEntity {
            return DeliverContractJpaEntity(
                id = deliverContract.id,
                agencyId = deliverContract.agencyId,
                deliverId = deliverContract.deliverId,
                serviceRegion = deliverContract.serviceRegion,
                expectedMonthlyVolume = deliverContract.expectedMonthlyVolume,
                unitPrice = deliverContract.unitPrice,
                startDate = deliverContract.startDate,
                endDate = deliverContract.endDate,
                memo = deliverContract.memo,
                status = deliverContract.status,
            )
        }
    }
}
