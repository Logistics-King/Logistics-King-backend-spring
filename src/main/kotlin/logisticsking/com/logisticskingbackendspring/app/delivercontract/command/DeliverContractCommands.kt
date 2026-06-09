package logisticsking.com.logisticskingbackendspring.app.delivercontract.command

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateDeliverContractCommand(
    val userId: UUID,
    val deliverId: UUID,
    val serviceRegion: String,
    val expectedMonthlyVolume: Int,
    val unitPrice: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val memo: String?,
)

data class UpdateDeliverContractCommand(
    val userId: UUID,
    val deliverContractId: UUID,
    val serviceRegion: String,
    val expectedMonthlyVolume: Int,
    val unitPrice: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val memo: String?,
)

data class DeliverContractIdCommand(
    val userId: UUID,
    val deliverContractId: UUID,
)
