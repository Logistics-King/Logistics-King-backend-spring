package logisticsking.com.logisticskingbackendspring.app.proposal.command

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import java.math.BigDecimal
import java.util.UUID

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
