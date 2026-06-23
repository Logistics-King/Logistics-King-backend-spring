package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.ListViewScope
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal

data class ContractRequestSearchCondition(
    val productName: String?,
    val productCategory: ProductCategory?,
    val boxSize: BoxSize?,
    val coldChainType: ColdChainType?,
    val status: ContractRequestStatus?,
    val pickupRegion: String?,
    val saturdayDeliveryRequired: Boolean?,
    val returnRequired: Boolean?,
    val scope: ListViewScope = ListViewScope.ALL,
    val nearbyRegions: List<String> = emptyList(),
    val minTargetUnitPrice: BigDecimal? = null,
    val maxTargetUnitPrice: BigDecimal? = null,
    val vendorName: String? = null,
) {
    val normalizedProductName: String? = productName?.trim()?.takeIf { it.isNotBlank() }
    val normalizedPickupRegion: String? = pickupRegion?.trim()?.takeIf { it.isNotBlank() }
    val normalizedNearbyRegions: List<String> = nearbyRegions.mapNotNull { it.trim().takeIf(String::isNotBlank) }.distinct()
    val normalizedVendorName: String? = vendorName?.trim()?.takeIf { it.isNotBlank() }
}
