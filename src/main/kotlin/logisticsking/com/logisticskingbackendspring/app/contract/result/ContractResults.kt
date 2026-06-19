package logisticsking.com.logisticskingbackendspring.app.contract.result

import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractStatus
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
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
    val boxSize: BoxSize,
    val unitPrice: BigDecimal,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
    val status: ContractStatus,
    val vendor: VendorResult?,
    val agency: AgencyResult?,
) {
    companion object {
        fun from(
            contract: Contract,
            vendor: Vendor? = null,
            agency: Agency? = null,
        ): ContractResult {
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
                vendor = vendor?.let(VendorResult::from),
                agency = agency?.let(AgencyResult::from),
            )
        }
    }
}
