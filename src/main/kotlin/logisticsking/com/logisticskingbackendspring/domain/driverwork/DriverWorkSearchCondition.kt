package logisticsking.com.logisticskingbackendspring.domain.driverwork

import java.time.LocalDate

data class DriverWorkSearchCondition(
    val status: DriverWorkStatus? = null,
    val serviceRegion: String? = null,
    val pickupStartDateFrom: LocalDate? = null,
    val pickupStartDateTo: LocalDate? = null,
) {
    val normalizedServiceRegion: String? = serviceRegion?.trim()?.takeIf(String::isNotBlank)
}
