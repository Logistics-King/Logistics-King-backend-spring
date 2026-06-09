package logisticsking.com.logisticskingbackendspring.app.contract.usecase

import logisticsking.com.logisticskingbackendspring.app.contract.command.CancelContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CreateContractRequestUseCase {
    fun create(command: CreateContractRequestCommand): ContractRequestResult
}

interface GetMyContractRequestsUseCase {
    fun getMyContractRequests(userId: UUID, pageable: Pageable): Page<ContractRequestResult>
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
