package logisticsking.com.logisticskingbackendspring.app.deliver.command

import java.util.UUID

data class CreateDeliverCommand(
    val userId: UUID,
    val agencyId: UUID,
    val driverName: String,
    val phoneNumber: String,
    val vehicleNumber: String?,
    val serviceRegions: List<String>,
    val active: Boolean,
    val memo: String?,
)

data class UpdateDeliverCommand(
    val userId: UUID,
    val agencyId: UUID,
    val driverName: String,
    val phoneNumber: String,
    val vehicleNumber: String?,
    val serviceRegions: List<String>,
    val active: Boolean,
    val memo: String?,
)
