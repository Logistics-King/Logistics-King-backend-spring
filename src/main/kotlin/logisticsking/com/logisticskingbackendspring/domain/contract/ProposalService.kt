package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.proposal.command.CreateProposalPriceOfferCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.DecideProposalNegotiationCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetContractRequestProposalsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetProposalNegotiationsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.SubmitProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.UpdateProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.WithdrawProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalNegotiationEventResult
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalResult
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.AcceptProposalNegotiationUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.CreateProposalPriceOfferUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetContractRequestProposalsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetMyProposalsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetOpenContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetProposalNegotiationsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.RejectProposalNegotiationUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.SubmitProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.UpdateProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.WithdrawProposalUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationPublisher
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationReferenceType
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationType
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
    private val proposalNegotiationEventRepository: ProposalNegotiationEventRepository,
    private val notificationPublisher: NotificationPublisher,
    private val idGenerator: IdGenerator,
) : GetOpenContractRequestsUseCase,
    SubmitProposalUseCase,
    GetContractRequestProposalsUseCase,
    GetMyProposalsUseCase,
    UpdateProposalUseCase,
    WithdrawProposalUseCase,
    GetProposalNegotiationsUseCase,
    CreateProposalPriceOfferUseCase,
    AcceptProposalNegotiationUseCase,
    RejectProposalNegotiationUseCase {

    @Transactional(readOnly = true)
    override fun getOpenContractRequests(userId: UUID, pageable: Pageable): Page<ContractRequestResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)

        return contractRequestRepository.findOpenVendorOffersForAgency(agency.id, pageable)
            .map(ContractRequestResult::from)
    }

    @Transactional
    override fun submit(command: SubmitProposalCommand): ProposalResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val contractRequest = findContractRequestForUpdate(command.contractRequestId)
        validateOpen(contractRequest)
        validateAgencyCanPropose(contractRequest, agency.id)
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

        val saved = proposalRepository.save(proposal)
        notificationPublisher.publish(
            receiverUserId = findVendorById(contractRequest.vendorId).userId,
            senderUserId = agency.userId,
            type = NotificationType.PROPOSAL_SUBMITTED,
            referenceType = NotificationReferenceType.PROPOSAL,
            referenceId = saved.id,
            linkUrl = "/contract-requests/${contractRequest.id}/proposals",
        )

        return ProposalResult.from(
            proposal = saved,
            agency = agency,
            vendor = findVendorById(saved.vendorId),
        )
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

        val proposals = proposalRepository.findAllByContractRequestId(contractRequest.id, pageable)
        val agenciesById = agencyRepository.findAllByIds(proposals.content.map(Proposal::agencyId).distinct())
            .associateBy(Agency::id)

        return proposals.map { proposal ->
            ProposalResult.from(
                proposal = proposal,
                agency = agenciesById[proposal.agencyId],
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getMyProposals(userId: UUID, pageable: Pageable): Page<ProposalResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)

        val proposals = proposalRepository.findAllByAgencyId(agency.id, pageable)
        val vendorsById = vendorRepository.findAllByIds(proposals.content.map(Proposal::vendorId).distinct())
            .associateBy(Vendor::id)

        return proposals.map { proposal ->
            ProposalResult.from(
                proposal = proposal,
                agency = agency,
                vendor = vendorsById[proposal.vendorId],
            )
        }
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

        val saved = proposalRepository.save(updated)
        notificationPublisher.publish(
            receiverUserId = findVendorById(saved.vendorId).userId,
            senderUserId = agency.userId,
            type = NotificationType.PROPOSAL_UPDATED,
            referenceType = NotificationReferenceType.PROPOSAL,
            referenceId = saved.id,
            linkUrl = "/contract-requests/${saved.contractRequestId}/proposals",
        )

        return ProposalResult.from(
            proposal = saved,
            agency = agency,
            vendor = findVendorById(saved.vendorId),
        )
    }

    @Transactional
    override fun withdraw(command: WithdrawProposalCommand): ProposalResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val proposal = findProposal(command.proposalId, agency.id)

        val saved = proposalRepository.save(proposal.withdraw())
        notificationPublisher.publish(
            receiverUserId = findVendorById(saved.vendorId).userId,
            senderUserId = agency.userId,
            type = NotificationType.PROPOSAL_WITHDRAWN,
            referenceType = NotificationReferenceType.PROPOSAL,
            referenceId = saved.id,
            linkUrl = "/contract-requests/${saved.contractRequestId}/proposals",
        )

        return ProposalResult.from(
            proposal = saved,
            agency = agency,
            vendor = findVendorById(saved.vendorId),
        )
    }

    @Transactional(readOnly = true)
    override fun getNegotiations(command: GetProposalNegotiationsCommand): List<ProposalNegotiationEventResult> {
        val proposal = proposalRepository.findById(command.proposalId)
            ?: throw GlobalException(ProposalErrorCode.NOT_FOUND)
        findNegotiationActor(command.userId, proposal)

        return proposalNegotiationEventRepository.findAllByProposalId(proposal.id)
            .map(ProposalNegotiationEventResult::from)
    }

    @Transactional
    override fun createPriceOffer(command: CreateProposalPriceOfferCommand): ProposalNegotiationEventResult {
        val proposal = proposalRepository.findByIdForUpdate(command.proposalId)
            ?: throw GlobalException(ProposalErrorCode.NOT_FOUND)
        val actorType = findNegotiationActor(command.userId, proposal)

        val event = ProposalNegotiationEvent.priceOffer(
            id = idGenerator.generate(),
            proposalId = proposal.id,
            sequence = proposal.nextSequence,
            actorType = actorType,
            unitPrice = command.unitPrice,
            memo = command.memo,
        )
        val updatedProposal = proposal.startPriceNegotiation(
            eventId = event.id,
            unitPrice = command.unitPrice,
        )

        val savedEvent = proposalNegotiationEventRepository.save(event)
        proposalRepository.save(updatedProposal)

        return ProposalNegotiationEventResult.from(savedEvent)
    }

    @Transactional
    override fun acceptNegotiation(command: DecideProposalNegotiationCommand): ProposalNegotiationEventResult {
        val proposal = proposalRepository.findByIdForUpdate(command.proposalId)
            ?: throw GlobalException(ProposalErrorCode.NOT_FOUND)
        val actorType = findNegotiationActor(command.userId, proposal)
        val pendingEvent = findPendingNegotiationEvent(command.eventId, proposal.id)
        validateNegotiationResponder(actorType, pendingEvent)

        val recordedEvent = ProposalNegotiationEvent.recorded(
            id = idGenerator.generate(),
            proposalId = proposal.id,
            sequence = proposal.nextSequence,
            actorType = actorType,
            eventType = ProposalNegotiationEventType.PRICE_ACCEPTED,
            memo = command.memo,
        )
        val updatedProposal = proposal.acceptPendingNegotiation(
            pendingEventId = pendingEvent.id,
            unitPrice = pendingEvent.unitPrice ?: throw GlobalException(ProposalErrorCode.INVALID_UNIT_PRICE),
        )

        proposalNegotiationEventRepository.save(pendingEvent.accept())
        val savedEvent = proposalNegotiationEventRepository.save(recordedEvent)
        proposalRepository.save(updatedProposal)

        return ProposalNegotiationEventResult.from(savedEvent)
    }

    @Transactional
    override fun rejectNegotiation(command: DecideProposalNegotiationCommand): ProposalNegotiationEventResult {
        val proposal = proposalRepository.findByIdForUpdate(command.proposalId)
            ?: throw GlobalException(ProposalErrorCode.NOT_FOUND)
        val actorType = findNegotiationActor(command.userId, proposal)
        val pendingEvent = findPendingNegotiationEvent(command.eventId, proposal.id)
        validateNegotiationResponder(actorType, pendingEvent)

        val recordedEvent = ProposalNegotiationEvent.recorded(
            id = idGenerator.generate(),
            proposalId = proposal.id,
            sequence = proposal.nextSequence,
            actorType = actorType,
            eventType = ProposalNegotiationEventType.PRICE_REJECTED,
            memo = command.memo,
        )
        val updatedProposal = proposal.rejectPendingNegotiation(pendingEvent.id)

        proposalNegotiationEventRepository.save(pendingEvent.reject())
        val savedEvent = proposalNegotiationEventRepository.save(recordedEvent)
        proposalRepository.save(updatedProposal)

        return ProposalNegotiationEventResult.from(savedEvent)
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

    private fun findVendorById(vendorId: UUID): Vendor {
        return vendorRepository.findById(vendorId)
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

    private fun validateAgencyCanPropose(
        contractRequest: ContractRequest,
        agencyId: UUID,
    ) {
        if (
            contractRequest.type != ContractRequestType.VENDOR_OFFER ||
            contractRequest.approverType != ContractPartyType.AGENCY ||
            (contractRequest.approverId != null && contractRequest.approverId != agencyId)
        ) {
            throw GlobalException(ProposalErrorCode.CONTRACT_REQUEST_NOT_FOUND)
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

    private fun findNegotiationActor(
        userId: UUID,
        proposal: Proposal,
    ): ContractPartyType {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(ProposalErrorCode.USER_NOT_FOUND)

        return when (user.role) {
            UserRole.VENDOR -> {
                val vendor = findVendorByUserId(userId)
                if (vendor.id != proposal.vendorId) {
                    throw GlobalException(ProposalErrorCode.INVALID_NEGOTIATION_ACTOR)
                }
                ContractPartyType.VENDOR
            }
            UserRole.AGENCY -> {
                val agency = findAgencyByUserId(userId)
                if (agency.id != proposal.agencyId) {
                    throw GlobalException(ProposalErrorCode.INVALID_NEGOTIATION_ACTOR)
                }
                ContractPartyType.AGENCY
            }
            else -> throw GlobalException(ProposalErrorCode.INVALID_NEGOTIATION_ACTOR)
        }
    }

    private fun findPendingNegotiationEvent(
        eventId: UUID,
        proposalId: UUID,
    ): ProposalNegotiationEvent {
        return proposalNegotiationEventRepository.findByIdAndProposalId(eventId, proposalId)
            ?: throw GlobalException(ProposalErrorCode.NEGOTIATION_EVENT_NOT_FOUND)
    }

    private fun validateNegotiationResponder(
        actorType: ContractPartyType,
        pendingEvent: ProposalNegotiationEvent,
    ) {
        if (pendingEvent.actorType == actorType) {
            throw GlobalException(ProposalErrorCode.INVALID_NEGOTIATION_RESPONDER)
        }
    }
}
