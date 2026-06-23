package logisticsking.com.logisticskingbackendspring.domain.agency

import logisticsking.com.logisticskingbackendspring.domain.common.ListViewScope

data class AgencySearchCondition(
    val agencyName: String? = null,

    val region: String? = null,

    val carrier: Carrier? = null,

    val saturdayDeliveryAvailable: Boolean? = null,

    val returnAvailable: Boolean? = null,

    val scope: ListViewScope = ListViewScope.ALL,
) {
    val normalizedAgencyName: String? = agencyName?.trim()?.takeIf(String::isNotBlank)

    val normalizedRegion: String? = region?.trim()?.takeIf(String::isNotBlank)
}
