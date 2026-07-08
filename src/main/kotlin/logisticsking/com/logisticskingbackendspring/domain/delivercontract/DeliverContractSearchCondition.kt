package logisticsking.com.logisticskingbackendspring.domain.delivercontract

import java.time.LocalDate

data class DeliverContractSearchCondition(
    val status: DeliverContractStatus? = null,
    val serviceRegion: String? = null,
    val startDateFrom: LocalDate? = null,
    val startDateTo: LocalDate? = null,
) {
    val normalizedServiceRegion: String? = serviceRegion?.trim()?.takeIf(String::isNotBlank)
}
