package logisticsking.com.logisticskingbackendspring.app.proposal.command

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import java.math.BigDecimal
import java.util.UUID

data class SubmitProposalCommand(
    val userId: UUID,
    val contractRequestId: UUID,
    val unitPrice: BigDecimal,
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
    val pickupStartTime: String,
    val pickupEndTime: String,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val coldChainType: ColdChainType,
    val memo: String?,
)

data class WithdrawProposalCommand(
    val userId: UUID,
    val proposalId: UUID,
)

data class GetContractRequestProposalsCommand(
    val userId: UUID,
    val contractRequestId: UUID,
)
