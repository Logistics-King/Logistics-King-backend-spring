package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory

data class ContractRequestSearchCondition(
    val productName: String?,
    val productCategory: ProductCategory?,
    val boxSize: BoxSize?,
    val coldChainType: ColdChainType?,
    val status: ContractRequestStatus?,
    val pickupRegion: String?,
    val saturdayDeliveryRequired: Boolean?,
    val returnRequired: Boolean?,
) {
    val normalizedProductName: String? = productName?.trim()?.takeIf { it.isNotBlank() }
    val normalizedPickupRegion: String? = pickupRegion?.trim()?.takeIf { it.isNotBlank() }
}
