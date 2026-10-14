package logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest

import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.util.UUID

sealed interface PublicVendorContractRequestResponse {
    data class Summary(val openContractRequestCount: Long, val newContractRequestCount: Long) : PublicVendorContractRequestResponse {
        companion object { fun from(value: PublicVendorContractRequestSummaryResult) = Summary(value.openContractRequestCount, value.newContractRequestCount) }
    }
    data class Item(val contractRequestId: UUID, val pickupRegion: String, val monthlyVolume: Int, val productCategory: ProductCategory, val boxSize: BoxSize, val pickupStartTime: String, val pickupEndTime: String, val targetUnitPrice: BigDecimal?, val saturdayDeliveryRequired: Boolean, val returnRequired: Boolean, val coldChainType: ColdChainType) : PublicVendorContractRequestResponse {
        companion object { fun from(value: PublicVendorContractRequestResult) = Item(value.contractRequestId, value.pickupRegion, value.monthlyVolume, value.productCategory, value.boxSize, value.pickupStartTime, value.pickupEndTime, value.targetUnitPrice, value.saturdayDeliveryRequired, value.returnRequired, value.coldChainType) }
    }
    data class List(val items: kotlin.collections.List<Item>, val page: Int, val size: Int, val totalElements: Long, val totalPages: Int, val hasNext: Boolean, val hasPrevious: Boolean) : PublicVendorContractRequestResponse {
        companion object { fun from(values: Page<PublicVendorContractRequestResult>): List { val page = PageResponse.from(values, Item::from); return List(page.items, page.page, page.size, page.totalElements, page.totalPages, page.hasNext, page.hasPrevious) } }
    }
}
