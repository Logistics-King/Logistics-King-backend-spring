package logisticsking.com.logisticskingbackendspring.app.proposal.command

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.ListViewScope
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class GetOpenContractRequestsCommand(
    val userId: UUID,
    val scope: ListViewScope,
    val pickupRegion: String?,
    val productName: String?,
    val productCategory: ProductCategory?,
    val boxSize: BoxSize?,
    val coldChainType: ColdChainType?,
    val saturdayDeliveryRequired: Boolean?,
    val returnRequired: Boolean?,
    val minTargetUnitPrice: BigDecimal?,
    val maxTargetUnitPrice: BigDecimal?,
    val vendorName: String?,
)

data class SubmitProposalCommand(
    val userId: UUID,
    val contractRequestId: UUID,
    val unitPrice: BigDecimal,
    val items: List<ProposalItemCommand>,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
)

data class UpdateProposalCommand(
    val userId: UUID,
    val proposalId: UUID,
    val unitPrice: BigDecimal,
    val items: List<ProposalItemCommand>,
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
)

data class ProposalItemCommand(
    val contractRequestItemId: UUID,
    val unitPrice: BigDecimal,
)

data class WithdrawProposalCommand(
    val userId: UUID,
    val proposalId: UUID,
)

data class GetContractRequestProposalsCommand(
    val userId: UUID,
    val contractRequestId: UUID,
)

data class GetProposalNegotiationsCommand(
    val userId: UUID,
    val proposalId: UUID,
)

data class CreateProposalPriceOfferCommand(
    val userId: UUID,
    val proposalId: UUID,
    val unitPrice: BigDecimal,
    val items: List<ProposalItemCommand>,
    val memo: String?,
)

data class DecideProposalNegotiationCommand(
    val userId: UUID,
    val proposalId: UUID,
    val eventId: UUID,
    val memo: String?,
)
