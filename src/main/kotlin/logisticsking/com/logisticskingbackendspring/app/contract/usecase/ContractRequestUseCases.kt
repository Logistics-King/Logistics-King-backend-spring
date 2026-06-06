package logisticsking.com.logisticskingbackendspring.app.contract.usecase

import logisticsking.com.logisticskingbackendspring.app.contract.command.CancelContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import java.util.UUID

interface CreateContractRequestUseCase {
    fun create(command: CreateContractRequestCommand): ContractRequestResult
}

interface GetMyContractRequestsUseCase {
    fun getMyContractRequests(userId: UUID): List<ContractRequestResult>
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
