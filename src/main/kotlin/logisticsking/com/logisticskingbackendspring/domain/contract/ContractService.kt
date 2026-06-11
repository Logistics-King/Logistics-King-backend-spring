package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.contract.command.AcceptProposalCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractResult
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.AcceptProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyAgencyContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyVendorContractsUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ContractService(
    private val userRepository: UserRepository,
    private val vendorRepository: VendorRepository,
    private val agencyRepository: AgencyRepository,
    private val contractRequestRepository: ContractRequestRepository,
    private val proposalRepository: ProposalRepository,
    private val contractRepository: ContractRepository,
    private val idGenerator: IdGenerator,
) : AcceptProposalUseCase,
    GetMyVendorContractsUseCase,
    GetMyAgencyContractsUseCase {

    @Transactional
    override fun accept(command: AcceptProposalCommand): ContractResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val proposal = proposalRepository.findByIdAndVendorId(
            id = command.proposalId,
            vendorId = vendor.id,
        ) ?: throw GlobalException(ContractErrorCode.PROPOSAL_NOT_FOUND)

        val contractRequest = contractRequestRepository.findByIdAndVendorIdForUpdate(
            id = proposal.contractRequestId,
            vendorId = vendor.id,
        ) ?: throw GlobalException(ContractErrorCode.CONTRACT_REQUEST_NOT_FOUND)

        if (contractRepository.existsByContractRequestId(contractRequest.id)) {
            throw GlobalException(ContractErrorCode.CONTRACT_ALREADY_EXISTS)
        }

        val proposals = proposalRepository.findAllByContractRequestIdForUpdate(contractRequest.id)
        val selectedProposal = proposals.firstOrNull { current ->
            current.id == command.proposalId && current.vendorId == vendor.id
        } ?: throw GlobalException(ContractErrorCode.PROPOSAL_NOT_FOUND)

        val contract = Contract.create(
            id = idGenerator.generate(),
            contractRequest = contractRequest,
            proposal = selectedProposal,
        )
        val proposalResults = proposals.map { current ->
            when {
                current.id == selectedProposal.id -> current.accept()
                current.status == ProposalStatus.SUBMITTED -> current.reject()
                else -> current
            }
        }

        proposalRepository.saveAll(proposalResults)
        contractRequestRepository.save(contractRequest.contract())

        return ContractResult.from(contractRepository.save(contract))
    }

    @Transactional(readOnly = true)
    override fun getMyVendorContracts(userId: UUID, pageable: Pageable): Page<ContractResult> {
        findVendorUser(userId)
        val vendor = findVendorByUserId(userId)

        return contractRepository.findAllByVendorId(vendor.id, pageable)
            .map(ContractResult::from)
    }

    @Transactional(readOnly = true)
    override fun getMyAgencyContracts(userId: UUID, pageable: Pageable): Page<ContractResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)

        return contractRepository.findAllByAgencyId(agency.id, pageable)
            .map(ContractResult::from)
    }

    private fun findVendorUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(ContractErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.VENDOR) {
            throw GlobalException(ContractErrorCode.USER_IS_NOT_VENDOR)
        }

        return user
    }

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(ContractErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(ContractErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(ContractErrorCode.VENDOR_NOT_FOUND)
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(ContractErrorCode.AGENCY_NOT_FOUND)
    }
}
