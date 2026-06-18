package logisticsking.com.logisticskingbackendspring.app.proposal.result

import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.contract.Proposal
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalStatus
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import java.math.BigDecimal
import java.util.UUID

data class ProposalResult(
    val proposalId: UUID,
    val contractRequestId: UUID,
    val vendorId: UUID,
    val agencyId: UUID,
    val unitPrice: BigDecimal,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
    val status: ProposalStatus,
    val agency: AgencyResult?,
    val vendor: VendorResult?,
) {
    companion object {
        fun from(
            proposal: Proposal,
            agency: Agency? = null,
            vendor: Vendor? = null,
        ): ProposalResult {
            return ProposalResult(
                proposalId = proposal.id,
                contractRequestId = proposal.contractRequestId,
                vendorId = proposal.vendorId,
                agencyId = proposal.agencyId,
                unitPrice = proposal.unitPrice,
                pickupStartTime = proposal.pickupStartTime,
                pickupEndTime = proposal.pickupEndTime,
                saturdayDeliveryAvailable = proposal.saturdayDeliveryAvailable,
                returnAvailable = proposal.returnAvailable,
                coldChainType = proposal.coldChainType,
                memo = proposal.memo,
                status = proposal.status,
                agency = agency?.let(AgencyResult::from),
                vendor = vendor?.let(VendorResult::from),
            )
        }
    }
}
