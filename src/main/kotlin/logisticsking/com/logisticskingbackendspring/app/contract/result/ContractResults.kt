package logisticsking.com.logisticskingbackendspring.app.contract.result

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractStatus
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class ContractResult(
    val contractId: UUID,
    val contractRequestId: UUID,
    val proposalId: UUID,
    val vendorId: UUID,
    val agencyId: UUID,
    val pickupRegion: String,
    val pickupAddress: String?,
    val monthlyVolume: Int,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: String,
    val unitPrice: BigDecimal,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
    val status: ContractStatus,
) {
    companion object {
        fun from(contract: Contract): ContractResult {
            return ContractResult(
                contractId = contract.id,
                contractRequestId = contract.contractRequestId,
                proposalId = contract.proposalId,
                vendorId = contract.vendorId,
                agencyId = contract.agencyId,
                pickupRegion = contract.pickupRegion,
                pickupAddress = contract.pickupAddress,
                monthlyVolume = contract.monthlyVolume,
                productCategory = contract.productCategory,
                productName = contract.productName,
                boxSize = contract.boxSize,
                unitPrice = contract.unitPrice,
                pickupStartTime = contract.pickupStartTime,
                pickupEndTime = contract.pickupEndTime,
                saturdayDeliveryAvailable = contract.saturdayDeliveryAvailable,
                returnAvailable = contract.returnAvailable,
                coldChainType = contract.coldChainType,
                memo = contract.memo,
                status = contract.status,
            )
        }
    }
}
