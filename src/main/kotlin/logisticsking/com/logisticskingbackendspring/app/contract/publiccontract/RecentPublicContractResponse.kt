package logisticsking.com.logisticskingbackendspring.app.contract.publiccontract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.time.LocalDateTime

data class RecentPublicContractResponse(val contracts: List<Item>) {
    data class Item(val businessName: String, val agencyName: String, val pickupRegion: String, val monthlyVolume: Int, val productCategory: ProductCategory, val boxSize: BoxSize, val unitPrice: BigDecimal, val pickupStartTime: String, val pickupEndTime: String, val saturdayDeliveryAvailable: Boolean, val returnAvailable: Boolean, val coldChainType: ColdChainType, val contractedAt: LocalDateTime) {
        companion object { fun from(value: RecentPublicContractResult) = Item(value.businessName, value.agencyName, value.pickupRegion, value.monthlyVolume, value.productCategory, value.boxSize, value.unitPrice, value.pickupStartTime, value.pickupEndTime, value.saturdayDeliveryAvailable, value.returnAvailable, value.coldChainType, value.contractedAt) }
    }
    companion object { fun from(values: List<RecentPublicContractResult>) = RecentPublicContractResponse(values.map(Item::from)) }
}
