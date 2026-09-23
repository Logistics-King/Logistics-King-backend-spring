package logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class PublicVendorContractRequestResult(
    val contractRequestId: UUID, val pickupRegion: String, val monthlyVolume: Int,
    val productCategory: ProductCategory, val boxSize: BoxSize, val pickupStartTime: String,
    val pickupEndTime: String, val targetUnitPrice: BigDecimal?, val saturdayDeliveryRequired: Boolean,
    val returnRequired: Boolean, val coldChainType: ColdChainType,
) {
    companion object {
        fun from(value: ContractRequest) = PublicVendorContractRequestResult(
            value.id, value.pickupRegion, value.monthlyVolume, value.productCategory, value.boxSize,
            value.pickupStartTime, value.pickupEndTime, value.targetUnitPrice,
            value.saturdayDeliveryRequired, value.returnRequired, value.coldChainType,
        )
    }
}

data class PublicVendorContractRequestSummaryResult(
    val openContractRequestCount: Long,
    val newContractRequestCount: Long,
)
