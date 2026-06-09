package logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase

import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.CreateDeliverContractCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.DeliverContractIdCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.UpdateDeliverContractCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.result.DeliverContractResult
import java.util.UUID

interface CreateDeliverContractUseCase {
    fun create(command: CreateDeliverContractCommand): DeliverContractResult
}

interface GetMyAgencyDeliverContractsUseCase {
    fun getMyAgencyDeliverContracts(userId: UUID): List<DeliverContractResult>
}

interface GetMyDriverDeliverContractsUseCase {
    fun getMyDriverDeliverContracts(userId: UUID): List<DeliverContractResult>
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
