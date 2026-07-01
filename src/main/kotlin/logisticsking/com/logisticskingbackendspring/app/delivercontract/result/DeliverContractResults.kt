package logisticsking.com.logisticskingbackendspring.app.delivercontract.result

import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.app.deliver.result.DeliverResult
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
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
    val agency: AgencyResult?,
    val deliver: DeliverResult?,
) {
    companion object {
        fun from(
            deliverContract: DeliverContract,
            agency: Agency? = null,
            deliver: Deliver? = null,
        ): DeliverContractResult {
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
                agency = agency?.let(AgencyResult::from),
                deliver = deliver?.let(DeliverResult::from),
            )
        }
    }
}
