package logisticsking.com.logisticskingbackendspring.app.deliver.command

import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverEmploymentType
import java.util.UUID

data class CreateDeliverCommand(
    val userId: UUID,
    val employmentType: DeliverEmploymentType,
    val agencyId: UUID?,
    val driverName: String,
    val phoneNumber: String,
    val vehicleNumber: String?,
    val serviceRegions: List<String>,
    val active: Boolean,
    val memo: String?,
)

data class UpdateDeliverCommand(
    val userId: UUID,
    val employmentType: DeliverEmploymentType,
    val agencyId: UUID?,
    val driverName: String,
    val phoneNumber: String,
    val vehicleNumber: String?,
    val serviceRegions: List<String>,
    val active: Boolean,
    val memo: String?,
)
