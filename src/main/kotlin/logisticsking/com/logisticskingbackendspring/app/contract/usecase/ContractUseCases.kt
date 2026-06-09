package logisticsking.com.logisticskingbackendspring.app.contract.usecase

import logisticsking.com.logisticskingbackendspring.app.contract.command.AcceptProposalCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface AcceptProposalUseCase {
    fun accept(command: AcceptProposalCommand): ContractResult
}

interface GetMyVendorContractsUseCase {
    fun getMyVendorContracts(userId: UUID, pageable: Pageable): Page<ContractResult>
}

interface GetMyAgencyContractsUseCase {
    fun getMyAgencyContracts(userId: UUID, pageable: Pageable): Page<ContractResult>
}
