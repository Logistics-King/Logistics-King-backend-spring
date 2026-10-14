package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.time.LocalDateTime

data class RecentPublicContract(
    val vendorBusinessName: String,
    val agencyName: String,
    val pickupRegion: String,
    val monthlyVolume: Int,
    val productCategory: ProductCategory,
    val boxSize: BoxSize,
    val unitPrice: BigDecimal,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val contractedAt: LocalDateTime,
)
