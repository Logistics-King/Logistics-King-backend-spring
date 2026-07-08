package logisticsking.com.logisticskingbackendspring.app.driverwork.result

import logisticsking.com.logisticskingbackendspring.app.deliver.result.DeliverResult
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWork
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplication
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkApplicationStatus
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class DriverWorkResult(
    val driverWorkId: UUID,
    val agencyId: UUID,
    val contractId: UUID,
    val title: String,
    val serviceRegion: String,
    val pickupStartDate: LocalDate,
    val pickupEndDate: LocalDate?,
    val expectedVolume: Int,
    val unitPrice: BigDecimal,
    val assignedDeliverId: UUID?,
    val status: DriverWorkStatus,
    val memo: String?,
    val assignedDeliver: DeliverResult?,
) {
    companion object {
        fun from(
            driverWork: DriverWork,
            assignedDeliver: Deliver? = null,
        ): DriverWorkResult {
            return DriverWorkResult(
                driverWorkId = driverWork.id,
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
                assignedDeliver = assignedDeliver?.let(DeliverResult::from),
            )
        }
    }
}

data class DriverWorkApplicationResult(
    val applicationId: UUID,
    val driverWorkId: UUID,
    val deliverId: UUID,
    val status: DriverWorkApplicationStatus,
    val memo: String?,
    val deliver: DeliverResult?,
    val driverWork: DriverWorkResult?,
) {
    companion object {
        fun from(
            application: DriverWorkApplication,
            deliver: Deliver? = null,
            driverWork: DriverWork? = null,
        ): DriverWorkApplicationResult {
            return DriverWorkApplicationResult(
                applicationId = application.id,
                driverWorkId = application.driverWorkId,
                deliverId = application.deliverId,
                status = application.status,
                memo = application.memo,
                deliver = deliver?.let(DeliverResult::from),
                driverWork = driverWork?.let(DriverWorkResult::from),
            )
        }
    }
}
