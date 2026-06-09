package logisticsking.com.logisticskingbackendspring.app.proposal.usecase

import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetContractRequestProposalsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.SubmitProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.UpdateProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.WithdrawProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface GetOpenContractRequestsUseCase {
    fun getOpenContractRequests(userId: UUID, pageable: Pageable): Page<ContractRequestResult>
}

interface SubmitProposalUseCase {
    fun submit(command: SubmitProposalCommand): ProposalResult
}

interface GetContractRequestProposalsUseCase {
    fun getContractRequestProposals(
        command: GetContractRequestProposalsCommand,
        pageable: Pageable,
    ): Page<ProposalResult>
}

interface GetMyProposalsUseCase {
    fun getMyProposals(userId: UUID, pageable: Pageable): Page<ProposalResult>
}

interface UpdateProposalUseCase {
    fun update(command: UpdateProposalCommand): ProposalResult
}

interface WithdrawProposalUseCase {
    fun withdraw(command: WithdrawProposalCommand): ProposalResult
}
