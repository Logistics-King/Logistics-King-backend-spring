package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType

data class ProductSearchCondition(
    val name: String?,
    val category: ProductCategory?,
    val boxSize: BoxSize?,
    val coldChainType: ColdChainType?,
) {
    val normalizedName: String? = name?.trim()?.takeIf { it.isNotBlank() }
}
