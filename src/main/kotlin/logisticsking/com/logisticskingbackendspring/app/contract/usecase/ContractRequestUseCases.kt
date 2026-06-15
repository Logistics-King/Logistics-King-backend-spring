package logisticsking.com.logisticskingbackendspring.app.contract.usecase

import logisticsking.com.logisticskingbackendspring.app.contract.command.CancelContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.ContractRequestDecisionCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetReceivedContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CreateContractRequestUseCase {
    fun create(command: CreateContractRequestCommand): ContractRequestResult
}

interface GetMyContractRequestsUseCase {
    fun getMyContractRequests(userId: UUID, pageable: Pageable): Page<ContractRequestResult>
}

interface GetReceivedContractRequestsUseCase {
    fun getReceivedContractRequests(
        command: GetReceivedContractRequestsCommand,
        pageable: Pageable,
    ): Page<ContractRequestResult>
}

interface GetContractRequestUseCase {
    fun get(command: GetContractRequestCommand): ContractRequestResult
}

interface UpdateContractRequestUseCase {
    fun update(command: UpdateContractRequestCommand): ContractRequestResult
}

interface CancelContractRequestUseCase {
    fun cancel(command: CancelContractRequestCommand): ContractRequestResult
}

interface AcceptContractRequestUseCase {
    fun accept(command: ContractRequestDecisionCommand): ContractResult
}

interface RejectContractRequestUseCase {
    fun reject(command: ContractRequestDecisionCommand): ContractRequestResult
}
