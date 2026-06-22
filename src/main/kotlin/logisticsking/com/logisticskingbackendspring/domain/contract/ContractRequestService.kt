package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.contract.command.CancelContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.ContractRequestDecisionCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.ContractRequestItemCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetMyContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetReceivedContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractResult
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.AcceptContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CancelContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CreateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetReceivedContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.RejectContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.UpdateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductRepository
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ContractRequestService(
    private val userRepository: UserRepository,
    private val vendorRepository: VendorRepository,
    private val agencyRepository: AgencyRepository,
    private val vendorProductRepository: VendorProductRepository,
    private val contractRequestRepository: ContractRequestRepository,
    private val proposalRepository: ProposalRepository,
    private val contractRepository: ContractRepository,
    private val idGenerator: IdGenerator,
) : CreateContractRequestUseCase,
    GetMyContractRequestsUseCase,
    GetReceivedContractRequestsUseCase,
    GetContractRequestUseCase,
    UpdateContractRequestUseCase,
    CancelContractRequestUseCase,
    AcceptContractRequestUseCase,
    RejectContractRequestUseCase {

    @Transactional
    override fun create(command: CreateContractRequestCommand): ContractRequestResult {
        val requester = findRequester(command.userId, command.type)
        validateApprover(command.type, command.approverId)
        val vendorId = vendorIdOf(command.type, requester.id, command.approverId)
        validateAndLockProducts(command.productIds(), vendorId)
        validateUnlockedProducts(command.productIds(), vendorId)
        val items = createItems(command.items)

        val contractRequest = ContractRequest.create(
            id = idGenerator.generate(),
            type = command.type,
            requesterId = requester.id,
            approverId = command.approverId,
            productId = command.productId,
            pickupRegion = command.pickupRegion,
            pickupAddress = command.pickupAddress,
            monthlyVolume = command.monthlyVolume,
            productCategory = command.productCategory,
            productName = command.productName,
            boxSize = command.boxSize,
            pickupStartTime = command.pickupStartTime,
            pickupEndTime = command.pickupEndTime,
            saturdayDeliveryRequired = command.saturdayDeliveryRequired,
            returnRequired = command.returnRequired,
            coldChainType = command.coldChainType,
            targetUnitPrice = command.targetUnitPrice,
            memo = command.memo,
            items = items,
        )

        return ContractRequestResult.from(contractRequestRepository.save(contractRequest))
    }

    @Transactional(readOnly = true)
    override fun getMyContractRequests(
        command: GetMyContractRequestsCommand,
        pageable: Pageable,
    ): Page<ContractRequestResult> {
        val party = findParty(command.userId)

        return contractRequestRepository.findAllByRequester(
            requesterType = party.type,
            requesterId = party.id,
            condition = ContractRequestSearchCondition(
                productName = command.productName,
                productCategory = command.productCategory,
                boxSize = command.boxSize,
                coldChainType = command.coldChainType,
                status = command.status,
                pickupRegion = command.pickupRegion,
                saturdayDeliveryRequired = command.saturdayDeliveryRequired,
                returnRequired = command.returnRequired,
            ),
            pageable = pageable,
        ).map(ContractRequestResult::from)
    }

    @Transactional(readOnly = true)
    override fun getReceivedContractRequests(
        command: GetReceivedContractRequestsCommand,
        pageable: Pageable,
    ): Page<ContractRequestResult> {
        val party = findParty(command.userId)

        return contractRequestRepository.findAllByApprover(
            approverType = party.type,
            approverId = party.id,
            pageable = pageable,
        ).map(ContractRequestResult::from)
    }

    @Transactional(readOnly = true)
    override fun get(command: GetContractRequestCommand): ContractRequestResult {
        val party = findParty(command.userId)
        val contractRequest = contractRequestRepository.findById(command.contractRequestId)
            ?: throw GlobalException(ContractRequestErrorCode.NOT_FOUND)
        if (!contractRequest.isParticipant(party.type, party.id)) {
            throw GlobalException(ContractRequestErrorCode.NOT_FOUND)
        }

        return ContractRequestResult.from(contractRequest)
    }

    @Transactional
    override fun update(command: UpdateContractRequestCommand): ContractRequestResult {
        val party = findParty(command.userId)
        val contractRequest = findContractRequestForUpdate(command.contractRequestId, party)
        validateAndLockProducts(command.productIds(), contractRequest.vendorId)
        validateUnlockedProducts(
            productIds = command.productIds(),
            vendorId = contractRequest.vendorId,
            excludedContractRequestId = contractRequest.id,
        )
        val items = createItems(command.items)

        val updated = contractRequest.update(
            productId = command.productId,
            pickupRegion = command.pickupRegion,
            pickupAddress = command.pickupAddress,
            monthlyVolume = command.monthlyVolume,
            productCategory = command.productCategory,
            productName = command.productName,
            boxSize = command.boxSize,
            pickupStartTime = command.pickupStartTime,
            pickupEndTime = command.pickupEndTime,
            saturdayDeliveryRequired = command.saturdayDeliveryRequired,
            returnRequired = command.returnRequired,
            coldChainType = command.coldChainType,
            targetUnitPrice = command.targetUnitPrice,
            memo = command.memo,
            items = items,
        )

        return ContractRequestResult.from(contractRequestRepository.save(updated))
    }

    @Transactional
    override fun cancel(command: CancelContractRequestCommand): ContractRequestResult {
        val party = findParty(command.userId)
        val contractRequest = findContractRequestForUpdate(command.contractRequestId, party)

        return ContractRequestResult.from(contractRequestRepository.save(contractRequest.cancel()))
    }

    @Transactional
    override fun accept(command: ContractRequestDecisionCommand): ContractResult {
        val party = findParty(command.userId)
        val contractRequest = findContractRequestForApproval(command.contractRequestId, party)
        val unitPrice = contractRequest.targetUnitPrice
            ?: throw GlobalException(ContractRequestErrorCode.TARGET_UNIT_PRICE_REQUIRED)
        if (contractRepository.existsByContractRequestId(contractRequest.id)) {
            throw GlobalException(ContractErrorCode.CONTRACT_ALREADY_EXISTS)
        }

        val proposal = Proposal.create(
            id = idGenerator.generate(),
            contractRequestId = contractRequest.id,
            vendorId = contractRequest.vendorId,
            agencyId = contractRequest.agencyId
                ?: throw GlobalException(ContractRequestErrorCode.INVALID_CONTRACT_PARTY),
            unitPrice = unitPrice,
            pickupStartTime = contractRequest.pickupStartTime,
            pickupEndTime = contractRequest.pickupEndTime,
            saturdayDeliveryAvailable = contractRequest.saturdayDeliveryRequired,
            returnAvailable = contractRequest.returnRequired,
            coldChainType = contractRequest.coldChainType,
            memo = contractRequest.memo,
            status = ProposalStatus.ACCEPTED,
        )
        val savedProposal = proposalRepository.save(proposal)
        val savedRequest = contractRequestRepository.save(contractRequest.contract())
        val contractItems = savedRequest.items.map { item ->
            ContractItem.fromRequestItem(
                id = idGenerator.generate(),
                item = item,
                unitPrice = savedProposal.unitPrice,
            )
        }
        val contract = Contract.create(
            id = idGenerator.generate(),
            contractRequest = savedRequest,
            proposal = savedProposal,
            items = contractItems,
        )

        return ContractResult.from(contractRepository.save(contract))
    }

    @Transactional
    override fun reject(command: ContractRequestDecisionCommand): ContractRequestResult {
        val party = findParty(command.userId)
        val contractRequest = findContractRequestForApproval(command.contractRequestId, party)

        return ContractRequestResult.from(contractRequestRepository.save(contractRequest.reject()))
    }

    private fun findRequester(
        userId: UUID,
        type: ContractRequestType,
    ): ContractParty {
        val party = findParty(userId)
        if (party.type != type.requesterType) {
            throw GlobalException(ContractRequestErrorCode.INVALID_CONTRACT_PARTY)
        }

        return party
    }

    private fun findParty(userId: UUID): ContractParty {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(ContractRequestErrorCode.USER_NOT_FOUND)

        return when (user.role) {
            UserRole.VENDOR -> ContractParty(
                type = ContractPartyType.VENDOR,
                id = findVendorByUserId(userId).id,
            )
            UserRole.AGENCY -> ContractParty(
                type = ContractPartyType.AGENCY,
                id = agencyRepository.findByUserId(userId)?.id
                    ?: throw GlobalException(ContractRequestErrorCode.AGENCY_NOT_FOUND),
            )
            else -> throw GlobalException(ContractRequestErrorCode.USER_ROLE_NOT_SUPPORTED)
        }
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(ContractRequestErrorCode.VENDOR_NOT_FOUND)
    }

    private fun validateApprover(
        type: ContractRequestType,
        approverId: UUID?,
    ) {
        if (approverId == null) {
            return
        }

        when (type.approverType) {
            ContractPartyType.VENDOR -> vendorRepository.findById(approverId)
                ?: throw GlobalException(ContractRequestErrorCode.VENDOR_NOT_FOUND)
            ContractPartyType.AGENCY -> agencyRepository.findById(approverId)
                ?: throw GlobalException(ContractRequestErrorCode.AGENCY_NOT_FOUND)
        }
    }

    private fun vendorIdOf(
        type: ContractRequestType,
        requesterId: UUID,
        approverId: UUID?,
    ): UUID {
        return when (type.requesterType) {
            ContractPartyType.VENDOR -> requesterId
            ContractPartyType.AGENCY -> approverId
                ?: throw GlobalException(ContractRequestErrorCode.INVALID_CONTRACT_PARTY)
        }
    }

    private fun validateAndLockProducts(
        productIds: Set<UUID>,
        vendorId: UUID,
    ) {
        if (productIds.isEmpty()) {
            return
        }

        val lockedProductIds = vendorProductRepository.findAllByIdsAndVendorIdForUpdate(productIds, vendorId)
            .map { it.id }
            .toSet()
        if (lockedProductIds.size != productIds.size) {
            throw GlobalException(ContractRequestErrorCode.PRODUCT_NOT_FOUND)
        }
    }

    private fun validateUnlockedProducts(
        productIds: Set<UUID>,
        vendorId: UUID,
        excludedContractRequestId: UUID? = null,
    ) {
        if (productIds.isEmpty()) {
            return
        }

        if (
            contractRequestRepository.existsActiveByVendorIdAndProductIds(
                vendorId = vendorId,
                productIds = productIds,
                excludedContractRequestId = excludedContractRequestId,
            )
        ) {
            throw GlobalException(ContractRequestErrorCode.PRODUCT_ALREADY_LOCKED)
        }
    }

    private fun CreateContractRequestCommand.productIds(): Set<UUID> {
        return buildSet {
            productId?.let(::add)
            addAll(items.mapNotNull(ContractRequestItemCommand::productId))
        }
    }

    private fun UpdateContractRequestCommand.productIds(): Set<UUID> {
        return buildSet {
            productId?.let(::add)
            addAll(items.mapNotNull(ContractRequestItemCommand::productId))
        }
    }

    private fun createItems(items: List<ContractRequestItemCommand>): List<ContractRequestItem> {
        return items.map { item ->
            ContractRequestItem.create(
                id = idGenerator.generate(),
                productId = item.productId,
                productCategory = item.productCategory,
                productName = item.productName,
                boxSize = item.boxSize,
                boxQuantity = item.boxQuantity,
                itemQuantity = item.itemQuantity,
                averageWeightGram = item.averageWeightGram,
                fragile = item.fragile,
                liquid = item.liquid,
                freshFood = item.freshFood,
                coldChainType = item.coldChainType,
                targetUnitPrice = item.targetUnitPrice,
            )
        }
    }

    private fun findContractRequestForUpdate(
        contractRequestId: UUID,
        party: ContractParty,
    ): ContractRequest {
        return contractRequestRepository.findByIdAndRequesterForUpdate(
            id = contractRequestId,
            requesterType = party.type,
            requesterId = party.id,
        ) ?: throw GlobalException(ContractRequestErrorCode.NOT_FOUND)
    }

    private fun findContractRequestForApproval(
        contractRequestId: UUID,
        party: ContractParty,
    ): ContractRequest {
        return contractRequestRepository.findByIdAndApproverForUpdate(
            id = contractRequestId,
            approverType = party.type,
            approverId = party.id,
        ) ?: throw GlobalException(ContractRequestErrorCode.NOT_FOUND)
    }

    private data class ContractParty(
        val type: ContractPartyType,
        val id: UUID,
    )
}
