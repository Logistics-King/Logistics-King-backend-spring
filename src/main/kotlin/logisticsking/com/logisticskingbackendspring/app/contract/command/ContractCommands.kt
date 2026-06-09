package logisticsking.com.logisticskingbackendspring.app.contract.command

import java.util.UUID

data class AcceptProposalCommand(
    val userId: UUID,
    val proposalId: UUID,
)
