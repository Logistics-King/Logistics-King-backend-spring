package logisticsking.com.logisticskingbackendspring.app.deliver.result

import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverEmploymentType
import java.util.UUID

data class DeliverResult(
    val deliverId: UUID,
    val userId: UUID,
    val employmentType: DeliverEmploymentType,
    val agencyId: UUID?,
    val driverName: String,
    val phoneNumber: String,
    val vehicleNumber: String?,
    val serviceRegions: List<String>,
    val active: Boolean,
    val memo: String?,
    val agency: AgencyResult?,
) {
    companion object {
        fun from(
            deliver: Deliver,
            agency: Agency? = null,
        ): DeliverResult {
            return DeliverResult(
                deliverId = deliver.id,
                userId = deliver.userId,
                employmentType = deliver.employmentType,
                agencyId = deliver.agencyId,
                driverName = deliver.driverName,
                phoneNumber = deliver.phoneNumber,
                vehicleNumber = deliver.vehicleNumber,
                serviceRegions = deliver.serviceRegions,
                active = deliver.active,
                memo = deliver.memo,
                agency = agency?.let(AgencyResult::from),
            )
        }
    }
}
