package logisticsking.com.logisticskingbackendspring.app.driverwork.command

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateDriverWorkCommand(
    val userId: UUID,
    val contractId: UUID,
    val title: String,
    val serviceRegion: String,
    val pickupStartDate: LocalDate,
    val pickupEndDate: LocalDate?,
    val expectedVolume: Int,
    val unitPrice: BigDecimal,
    val assignedDeliverId: UUID?,
    val memo: String?,
)

data class DriverWorkIdCommand(
    val userId: UUID,
    val driverWorkId: UUID,
)

data class AssignDriverWorkCommand(
    val userId: UUID,
    val driverWorkId: UUID,
    val deliverId: UUID,
)

data class ApplyDriverWorkCommand(
    val userId: UUID,
    val driverWorkId: UUID,
    val memo: String?,
)

data class SelectDriverWorkApplicationCommand(
    val userId: UUID,
    val driverWorkId: UUID,
    val applicationId: UUID,
)
