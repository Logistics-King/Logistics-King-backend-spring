package logisticsking.com.logisticskingbackendspring.app.contract.result

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class ContractRequestResult(
    val contractRequestId: UUID,
    val vendorId: UUID,
    val agencyId: UUID?,
    val type: ContractRequestType,
    val requesterType: ContractPartyType,
    val requesterId: UUID,
    val approverType: ContractPartyType,
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
    val status: ContractRequestStatus,
) {
    companion object {
        fun from(contractRequest: ContractRequest): ContractRequestResult {
            return ContractRequestResult(
                contractRequestId = contractRequest.id,
                vendorId = contractRequest.vendorId,
                agencyId = contractRequest.agencyId,
                type = contractRequest.type,
                requesterType = contractRequest.requesterType,
                requesterId = contractRequest.requesterId,
                approverType = contractRequest.approverType,
                approverId = contractRequest.approverId,
                productId = contractRequest.productId,
                pickupRegion = contractRequest.pickupRegion,
                pickupAddress = contractRequest.pickupAddress,
                monthlyVolume = contractRequest.monthlyVolume,
                productCategory = contractRequest.productCategory,
                productName = contractRequest.productName,
                boxSize = contractRequest.boxSize,
                pickupStartTime = contractRequest.pickupStartTime,
                pickupEndTime = contractRequest.pickupEndTime,
                saturdayDeliveryRequired = contractRequest.saturdayDeliveryRequired,
                returnRequired = contractRequest.returnRequired,
                coldChainType = contractRequest.coldChainType,
                targetUnitPrice = contractRequest.targetUnitPrice,
                memo = contractRequest.memo,
                status = contractRequest.status,
            )
        }
    }
}
