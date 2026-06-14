package logisticsking.com.logisticskingbackendspring.domain.agency

data class AgencySearchCondition(
    val region: String? = null,
    val carrier: Carrier? = null,
    val saturdayDeliveryAvailable: Boolean? = null,
    val returnAvailable: Boolean? = null,
) {
    val normalizedRegion: String? = region?.trim()?.takeIf(String::isNotBlank)
}
