package logisticsking.com.logisticskingbackendspring.app.proposal.usecase

import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetContractRequestProposalsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.SubmitProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.UpdateProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.WithdrawProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalResult
import java.util.UUID

interface GetOpenContractRequestsUseCase {
    fun getOpenContractRequests(userId: UUID): List<ContractRequestResult>
}

interface SubmitProposalUseCase {
    fun submit(command: SubmitProposalCommand): ProposalResult
}

interface GetContractRequestProposalsUseCase {
    fun getContractRequestProposals(command: GetContractRequestProposalsCommand): List<ProposalResult>
}

interface GetMyProposalsUseCase {
    fun getMyProposals(userId: UUID): List<ProposalResult>
}

interface UpdateProposalUseCase {
    fun update(command: UpdateProposalCommand): ProposalResult
}

interface WithdrawProposalUseCase {
    fun withdraw(command: WithdrawProposalCommand): ProposalResult
}
