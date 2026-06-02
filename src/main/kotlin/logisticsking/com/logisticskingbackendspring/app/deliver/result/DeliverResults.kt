package logisticsking.com.logisticskingbackendspring.app.deliver.result

import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import java.util.UUID

data class DeliverResult(
    val deliverId: UUID,
    val userId: UUID,
    val agencyId: UUID,
    val driverName: String,
    val phoneNumber: String,
    val vehicleNumber: String?,
    val serviceRegions: List<String>,
    val active: Boolean,
    val memo: String?,
) {
    companion object {
        fun from(deliver: Deliver): DeliverResult {
            return DeliverResult(
                deliverId = deliver.id,
                userId = deliver.userId,
                agencyId = deliver.agencyId,
                driverName = deliver.driverName,
                phoneNumber = deliver.phoneNumber,
                vehicleNumber = deliver.vehicleNumber,
                serviceRegions = deliver.serviceRegions,
                active = deliver.active,
                memo = deliver.memo,
            )
        }
    }
}
