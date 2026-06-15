package logisticsking.com.logisticskingbackendspring.app.contract.command

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class CreateContractRequestCommand(
    val userId: UUID,
    val type: ContractRequestType,
    val approverId: UUID?,
    val productId: UUID?,
    val pickupRegion: String,
    val pickupAddress: String?,
    val monthlyVolume: Int,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: BoxSize,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryRequired: Boolean,
    val returnRequired: Boolean,
    val coldChainType: ColdChainType,
    val targetUnitPrice: BigDecimal?,
    val memo: String?,
    val items: List<ContractRequestItemCommand>,
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
    val boxSize: BoxSize,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryRequired: Boolean,
    val returnRequired: Boolean,
    val coldChainType: ColdChainType,
    val targetUnitPrice: BigDecimal?,
    val memo: String?,
    val items: List<ContractRequestItemCommand>,
)

data class ContractRequestItemCommand(
    val productId: UUID?,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: BoxSize,
    val boxQuantity: Int,
    val itemQuantity: Int,
    val averageWeightGram: Int?,
    val fragile: Boolean,
    val liquid: Boolean,
    val freshFood: Boolean,
    val coldChainType: ColdChainType,
    val targetUnitPrice: BigDecimal?,
)

data class GetContractRequestCommand(
    val userId: UUID,
    val contractRequestId: UUID,
)

data class GetReceivedContractRequestsCommand(
    val userId: UUID,
)

data class CancelContractRequestCommand(
    val userId: UUID,
    val contractRequestId: UUID,
)

data class ContractRequestDecisionCommand(
    val userId: UUID,
    val contractRequestId: UUID,
)
