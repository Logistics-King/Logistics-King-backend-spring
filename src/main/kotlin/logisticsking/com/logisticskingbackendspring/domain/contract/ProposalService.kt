package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetContractRequestProposalsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.SubmitProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.UpdateProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.WithdrawProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalResult
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetContractRequestProposalsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetMyProposalsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetOpenContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.SubmitProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.UpdateProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.WithdrawProposalUseCase
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
class ProposalService(
    private val userRepository: UserRepository,
    private val vendorRepository: VendorRepository,
    private val agencyRepository: AgencyRepository,
    private val contractRequestRepository: ContractRequestRepository,
    private val proposalRepository: ProposalRepository,
    private val idGenerator: IdGenerator,
) : GetOpenContractRequestsUseCase,
    SubmitProposalUseCase,
    GetContractRequestProposalsUseCase,
    GetMyProposalsUseCase,
    UpdateProposalUseCase,
    WithdrawProposalUseCase {

    @Transactional(readOnly = true)
    override fun getOpenContractRequests(userId: UUID, pageable: Pageable): Page<ContractRequestResult> {
        findAgencyUser(userId)
        findAgencyByUserId(userId)

        return contractRequestRepository.findAllByStatus(ContractRequestStatus.OPEN, pageable)
            .map(ContractRequestResult::from)
    }

    @Transactional
    override fun submit(command: SubmitProposalCommand): ProposalResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val contractRequest = findContractRequestForUpdate(command.contractRequestId)
        validateOpen(contractRequest)
        if (proposalRepository.existsByContractRequestIdAndAgencyId(contractRequest.id, agency.id)) {
            throw GlobalException(ProposalErrorCode.ALREADY_EXISTS)
        }

        val proposal = Proposal.create(
            id = idGenerator.generate(),
            contractRequestId = contractRequest.id,
            vendorId = contractRequest.vendorId,
            agencyId = agency.id,
            unitPrice = command.unitPrice,
            pickupStartTime = command.pickupStartTime,
            pickupEndTime = command.pickupEndTime,
            saturdayDeliveryAvailable = command.saturdayDeliveryAvailable,
            returnAvailable = command.returnAvailable,
            coldChainType = command.coldChainType,
            memo = command.memo,
        )

        return ProposalResult.from(proposalRepository.save(proposal))
    }

    @Transactional(readOnly = true)
    override fun getContractRequestProposals(
        command: GetContractRequestProposalsCommand,
        pageable: Pageable,
    ): Page<ProposalResult> {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val contractRequest = contractRequestRepository.findByIdAndVendorId(
            id = command.contractRequestId,
            vendorId = vendor.id,
        ) ?: throw GlobalException(ProposalErrorCode.CONTRACT_REQUEST_NOT_FOUND)

        return proposalRepository.findAllByContractRequestId(contractRequest.id, pageable)
            .map(ProposalResult::from)
    }

    @Transactional(readOnly = true)
    override fun getMyProposals(userId: UUID, pageable: Pageable): Page<ProposalResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)

        return proposalRepository.findAllByAgencyId(agency.id, pageable)
            .map(ProposalResult::from)
    }

    @Transactional
    override fun update(command: UpdateProposalCommand): ProposalResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val proposal = findProposal(command.proposalId, agency.id)
        val updated = proposal.update(
            unitPrice = command.unitPrice,
            pickupStartTime = command.pickupStartTime,
            pickupEndTime = command.pickupEndTime,
            saturdayDeliveryAvailable = command.saturdayDeliveryAvailable,
            returnAvailable = command.returnAvailable,
            coldChainType = command.coldChainType,
            memo = command.memo,
        )

        return ProposalResult.from(proposalRepository.save(updated))
    }

    @Transactional
    override fun withdraw(command: WithdrawProposalCommand): ProposalResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val proposal = findProposal(command.proposalId, agency.id)

        return ProposalResult.from(proposalRepository.save(proposal.withdraw()))
    }

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(ProposalErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(ProposalErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findVendorUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(ProposalErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.VENDOR) {
            throw GlobalException(ProposalErrorCode.USER_IS_NOT_VENDOR)
        }

        return user
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(ProposalErrorCode.AGENCY_NOT_FOUND)
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(ProposalErrorCode.VENDOR_NOT_FOUND)
    }

    private fun findContractRequestForUpdate(contractRequestId: UUID): ContractRequest {
        return contractRequestRepository.findByIdForUpdate(contractRequestId)
            ?: throw GlobalException(ProposalErrorCode.CONTRACT_REQUEST_NOT_FOUND)
    }

    private fun validateOpen(contractRequest: ContractRequest) {
        if (contractRequest.status != ContractRequestStatus.OPEN) {
            throw GlobalException(ProposalErrorCode.CONTRACT_REQUEST_IS_NOT_OPEN)
        }
    }

    private fun findProposal(
        proposalId: UUID,
        agencyId: UUID,
    ): Proposal {
        return proposalRepository.findByIdAndAgencyIdForUpdate(
            id = proposalId,
            agencyId = agencyId,
        ) ?: throw GlobalException(ProposalErrorCode.NOT_FOUND)
    }
}
