package logisticsking.com.logisticskingbackendspring.domain.deliver

data class DeliverSearchCondition(
    val active: Boolean? = null,
    val serviceRegion: String? = null,
    val driverName: String? = null,
    val vehicleNumber: String? = null,
) {
    val normalizedServiceRegion: String? = serviceRegion?.trim()?.takeIf(String::isNotBlank)
    val normalizedDriverName: String? = driverName?.trim()?.takeIf(String::isNotBlank)
    val normalizedVehicleNumber: String? = vehicleNumber?.trim()?.takeIf(String::isNotBlank)
}
