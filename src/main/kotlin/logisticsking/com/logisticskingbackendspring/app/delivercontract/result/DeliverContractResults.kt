package logisticsking.com.logisticskingbackendspring.app.delivercontract.result

import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContract
import logisticsking.com.logisticskingbackendspring.domain.delivercontract.DeliverContractStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class DeliverContractResult(
    val deliverContractId: UUID,
    val agencyId: UUID,
    val deliverId: UUID,
    val serviceRegion: String,
    val expectedMonthlyVolume: Int,
    val unitPrice: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val memo: String?,
    val status: DeliverContractStatus,
) {
    companion object {
        fun from(deliverContract: DeliverContract): DeliverContractResult {
            return DeliverContractResult(
                deliverContractId = deliverContract.id,
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
