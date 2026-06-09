package logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase

import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.CreateDeliverContractCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.DeliverContractIdCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.UpdateDeliverContractCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.result.DeliverContractResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CreateDeliverContractUseCase {
    fun create(command: CreateDeliverContractCommand): DeliverContractResult
}

interface GetMyAgencyDeliverContractsUseCase {
    fun getMyAgencyDeliverContracts(userId: UUID, pageable: Pageable): Page<DeliverContractResult>
}

interface GetMyDriverDeliverContractsUseCase {
    fun getMyDriverDeliverContracts(userId: UUID, pageable: Pageable): Page<DeliverContractResult>
}

interface UpdateDeliverContractUseCase {
    fun update(command: UpdateDeliverContractCommand): DeliverContractResult
}

interface AcceptDeliverContractUseCase {
    fun accept(command: DeliverContractIdCommand): DeliverContractResult
}

interface RejectDeliverContractUseCase {
    fun reject(command: DeliverContractIdCommand): DeliverContractResult
}

interface CancelDeliverContractUseCase {
    fun cancel(command: DeliverContractIdCommand): DeliverContractResult
}
