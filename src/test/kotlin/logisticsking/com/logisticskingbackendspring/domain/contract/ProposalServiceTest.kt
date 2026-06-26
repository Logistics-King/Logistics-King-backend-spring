package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetOpenContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.common.ListViewScope
import logisticsking.com.logisticskingbackendspring.domain.notification.Notification
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationPublisher
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationReferenceType
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationType
import logisticsking.com.logisticskingbackendspring.domain.notification.PublishNotificationCommand
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.UUID

class ProposalServiceTest {

    @Test
    fun `ADMIN은 대리점 프로필 없이 OPEN 계약 요청을 전체 조회한다`() {
        val admin = user(role = UserRole.ADMIN)
        val contractRequest = contractRequest(vendorId = UUID.randomUUID())
        val service = proposalService(
            userRepository = FakeUserRepository(admin),
            contractRequestRepository = FakeContractRequestRepository(contractRequest),
        )

        val results = service.getOpenContractRequests(
            GetOpenContractRequestsCommand(
                userId = admin.id,
                scope = ListViewScope.ALL,
                pickupRegion = null,
                productName = null,
                productCategory = null,
                boxSize = null,
                coldChainType = null,
                saturdayDeliveryRequired = null,
                returnRequired = null,
                minTargetUnitPrice = null,
                maxTargetUnitPrice = null,
                vendorName = null,
            ),
            Pageable.ofSize(20),
        )

        assertEquals(1, results.totalElements)
        assertEquals(contractRequest.id, results.content.first().contractRequestId)
    }

    private fun proposalService(
        userRepository: UserRepository = FakeUserRepository(),
        vendorRepository: VendorRepository = FakeVendorRepository(),
        agencyRepository: AgencyRepository = FakeAgencyRepository(),
        contractRequestRepository: ContractRequestRepository = FakeContractRequestRepository(),
        proposalRepository: ProposalRepository = FakeProposalRepository(),
        proposalNegotiationEventRepository: ProposalNegotiationEventRepository = FakeProposalNegotiationEventRepository(),
        notificationPublisher: NotificationPublisher = FakeNotificationPublisher(),
        idGenerator: IdGenerator = FixedIdGenerator(),
    ): ProposalService {
        return ProposalService(
            userRepository = userRepository,
            vendorRepository = vendorRepository,
            agencyRepository = agencyRepository,
            contractRequestRepository = contractRequestRepository,
            proposalRepository = proposalRepository,
            proposalNegotiationEventRepository = proposalNegotiationEventRepository,
            notificationPublisher = notificationPublisher,
            idGenerator = idGenerator,
        )
    }

    private fun contractRequest(
        id: UUID = UUID.randomUUID(),
        vendorId: UUID,
    ): ContractRequest {
        return ContractRequest.create(
            id = id,
            type = ContractRequestType.VENDOR_OFFER,
            requesterId = vendorId,
            approverId = null,
            productId = null,
            pickupRegion = "경기도 안산시 일동",
            pickupAddress = "경기도 안산시 상록구 일동 101호",
            monthlyVolume = 10,
            productCategory = ProductCategory.CLOTHING,
            productName = "의류",
            boxSize = BoxSize.SIZE_60,
            pickupStartTime = "09:00",
            pickupEndTime = "18:00",
            saturdayDeliveryRequired = true,
            returnRequired = true,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = BigDecimal("2000"),
            memo = null,
            items = listOf(contractRequestItem()),
        )
    }

    private fun contractRequestItem(): ContractRequestItem {
        return ContractRequestItem.create(
            id = UUID.randomUUID(),
            productId = null,
            productCategory = ProductCategory.CLOTHING,
            productName = "의류",
            boxSize = BoxSize.SIZE_60,
            boxQuantity = 10,
            itemQuantity = 0,
            averageWeightGram = 700,
            fragile = false,
            liquid = false,
            freshFood = false,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = BigDecimal("2000"),
        )
    }

    private fun user(
        id: UUID = UUID.randomUUID(),
        role: UserRole,
    ): User {
        return User.create(
            id = id,
            loginId = id.toString(),
            email = "$id@example.com",
            encodedPassword = "encoded-password",
            name = "사용자",
            role = role,
        )
    }

    private class FakeUserRepository(
        vararg seedUsers: User,
    ) : UserRepository {
        private val users = seedUsers.associateBy(User::id)

        override fun findById(id: UUID): User? = users[id]
        override fun findByLoginId(loginId: String): User? = users.values.firstOrNull { it.loginId == loginId }
        override fun findByNameAndEmail(name: String, email: String): User? = users.values.firstOrNull { it.name == name && it.email == email }
        override fun findByLoginIdAndEmail(loginId: String, email: String): User? = users.values.firstOrNull { it.loginId == loginId && it.email == email }
        override fun existsByLoginId(loginId: String): Boolean = users.values.any { it.loginId == loginId }
        override fun existsByEmail(email: String): Boolean = users.values.any { it.email == email }
        override fun save(user: User): User = user
        override fun updatePassword(id: UUID, encodedPassword: String): User? = users[id]?.changePassword(encodedPassword)
    }

    private class FakeVendorRepository : VendorRepository {
        override fun save(vendor: Vendor): Vendor = vendor
        override fun findById(id: UUID): Vendor? = null
        override fun findAllByIds(ids: Collection<UUID>): List<Vendor> = emptyList()
        override fun findByUserId(userId: UUID): Vendor? = null
        override fun existsByUserId(userId: UUID): Boolean = false
    }

    private class FakeAgencyRepository : AgencyRepository {
        override fun save(agency: Agency): Agency = agency
        override fun findById(id: UUID): Agency? = null
        override fun findAllByIds(ids: Collection<UUID>): List<Agency> = emptyList()
        override fun findAll(condition: AgencySearchCondition, pageable: Pageable): Page<Agency> = PageImpl(emptyList(), pageable, 0)
        override fun findAllForRecommendation(): List<Agency> = emptyList()
        override fun findByUserId(userId: UUID): Agency? = null
        override fun existsByUserId(userId: UUID): Boolean = false
    }

    private class FakeContractRequestRepository(
        vararg seedRequests: ContractRequest,
    ) : ContractRequestRepository {
        private val requests = seedRequests.associateBy(ContractRequest::id)

        override fun save(contractRequest: ContractRequest): ContractRequest = contractRequest
        override fun findById(id: UUID): ContractRequest? = requests[id]
        override fun findByIdForUpdate(id: UUID): ContractRequest? = requests[id]
        override fun findByIdAndRequesterForUpdate(id: UUID, requesterType: ContractPartyType, requesterId: UUID): ContractRequest? = null
        override fun findByIdAndApproverForUpdate(id: UUID, approverType: ContractPartyType, approverId: UUID): ContractRequest? = null
        override fun findByIdAndVendorId(id: UUID, vendorId: UUID): ContractRequest? = null
        override fun findByIdAndVendorIdForUpdate(id: UUID, vendorId: UUID): ContractRequest? = null
        override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<ContractRequest> = PageImpl(emptyList(), pageable, 0)
        override fun findAllByRequester(
            requesterType: ContractPartyType,
            requesterId: UUID,
            condition: ContractRequestSearchCondition,
            pageable: Pageable,
        ): Page<ContractRequest> = PageImpl(emptyList(), pageable, 0)
        override fun findAllByApprover(approverType: ContractPartyType, approverId: UUID, pageable: Pageable): Page<ContractRequest> = PageImpl(emptyList(), pageable, 0)
        override fun findAllByStatus(status: ContractRequestStatus, pageable: Pageable): Page<ContractRequest> = PageImpl(requests.values.filter { it.status == status }, pageable, requests.size.toLong())
        override fun findOpenVendorOffersForAgency(
            agencyId: UUID,
            condition: ContractRequestSearchCondition,
            pageable: Pageable,
        ): Page<ContractRequest> = PageImpl(emptyList(), pageable, 0)
        override fun findOpenVendorOffers(
            condition: ContractRequestSearchCondition,
            pageable: Pageable,
        ): Page<ContractRequest> = PageImpl(requests.values.filter { it.status == ContractRequestStatus.OPEN }, pageable, requests.size.toLong())
        override fun existsActiveByVendorIdAndProductIds(
            vendorId: UUID,
            productIds: Collection<UUID>,
            excludedContractRequestId: UUID?,
        ): Boolean = false
    }

    private class FakeProposalRepository : ProposalRepository {
        override fun save(proposal: Proposal): Proposal = proposal
        override fun findById(id: UUID): Proposal? = null
        override fun findByIdForUpdate(id: UUID): Proposal? = null
        override fun findByIdAndAgencyId(id: UUID, agencyId: UUID): Proposal? = null
        override fun findByIdAndAgencyIdForUpdate(id: UUID, agencyId: UUID): Proposal? = null
        override fun findByIdAndVendorId(id: UUID, vendorId: UUID): Proposal? = null
        override fun findAllByContractRequestId(contractRequestId: UUID): List<Proposal> = emptyList()
        override fun findAllByContractRequestIdForUpdate(contractRequestId: UUID): List<Proposal> = emptyList()
        override fun findAllByContractRequestId(contractRequestId: UUID, pageable: Pageable): Page<Proposal> = PageImpl(emptyList(), pageable, 0)
        override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Proposal> = PageImpl(emptyList(), pageable, 0)
        override fun saveAll(proposals: List<Proposal>): List<Proposal> = proposals
        override fun existsByContractRequestIdAndAgencyId(contractRequestId: UUID, agencyId: UUID): Boolean = false
    }

    private class FakeProposalNegotiationEventRepository : ProposalNegotiationEventRepository {
        override fun save(event: ProposalNegotiationEvent): ProposalNegotiationEvent = event
        override fun findByIdAndProposalId(id: UUID, proposalId: UUID): ProposalNegotiationEvent? = null
        override fun findAllByProposalId(proposalId: UUID): List<ProposalNegotiationEvent> = emptyList()
    }

    private class FakeNotificationPublisher : NotificationPublisher {
        override fun publish(
            receiverUserId: UUID,
            senderUserId: UUID?,
            type: NotificationType,
            referenceType: NotificationReferenceType?,
            referenceId: UUID?,
            linkUrl: String?,
        ): Notification {
            return Notification.create(
                id = UUID.randomUUID(),
                receiverUserId = receiverUserId,
                senderUserId = senderUserId,
                type = type,
                title = "알림",
                message = "알림",
                linkUrl = linkUrl,
                referenceType = referenceType,
                referenceId = referenceId,
            )
        }

        override fun publishAll(commands: List<PublishNotificationCommand>): List<Notification> = emptyList()
    }

    private class FixedIdGenerator : IdGenerator {
        override fun generate(): UUID = UUID.randomUUID()
    }
}
