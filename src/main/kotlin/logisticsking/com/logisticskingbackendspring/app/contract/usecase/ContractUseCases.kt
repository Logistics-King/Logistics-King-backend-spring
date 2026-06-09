package logisticsking.com.logisticskingbackendspring.app.contract.usecase

import logisticsking.com.logisticskingbackendspring.app.contract.command.AcceptProposalCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractResult
import java.util.UUID

interface AcceptProposalUseCase {
    fun accept(command: AcceptProposalCommand): ContractResult
}

interface GetMyVendorContractsUseCase {
    fun getMyVendorContracts(userId: UUID): List<ContractResult>
}

interface GetMyAgencyContractsUseCase {
    fun getMyAgencyContracts(userId: UUID): List<ContractResult>
}
