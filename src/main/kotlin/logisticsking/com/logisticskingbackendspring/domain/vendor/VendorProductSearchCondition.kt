package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.ListViewScope

data class VendorProductSearchCondition(
    val name: String?,
    val category: ProductCategory?,
    val boxSize: BoxSize?,
    val coldChainType: ColdChainType?,
    val scope: ListViewScope = ListViewScope.ALL,
) {
    val normalizedName: String? = name?.trim()?.takeIf { it.isNotBlank() }
}
