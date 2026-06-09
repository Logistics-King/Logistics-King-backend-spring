package logisticsking.com.logisticskingbackendspring.app.contract.command

import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class CreateContractRequestCommand(
    val userId: UUID,
    val productId: UUID?,
    val pickupRegion: String,
    val pickupAddress: String?,
    val monthlyVolume: Int,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: String,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryRequired: Boolean,
    val returnRequired: Boolean,
    val coldChainRequired: Boolean,
    val targetUnitPrice: BigDecimal?,
    val memo: String?,
)

data class UpdateContractRequestCommand(
    val userId: UUID,
    val contractRequestId: UUID,
    val productId: UUID?,
    val pickupRegion: String,
    val pickupAddress: String?,
    val monthlyVolume: Int,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: String,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryRequired: Boolean,
    val returnRequired: Boolean,
    val coldChainRequired: Boolean,
    val targetUnitPrice: BigDecimal?,
    val memo: String?,
)

data class GetContractRequestCommand(
    val userId: UUID,
    val contractRequestId: UUID,
)

data class CancelContractRequestCommand(
    val userId: UUID,
    val contractRequestId: UUID,
)
