package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.contract.command.ContractRequestItemCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetMyContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProduct
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductRepository
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.UUID

class ContractRequestServiceTest {

    @Test
    fun `create 시 이미 활성 계약 요청에 묶인 배송 품목이면 예외가 발생한다`() {
        val vendorUser = user(role = UserRole.VENDOR)
        val vendor = vendor(userId = vendorUser.id)
        val product = vendorProduct(vendorId = vendor.id)
        val contractRequestRepository = FakeContractRequestRepository(
            contractRequest(vendorId = vendor.id, productId = product.id),
        )
        val service = contractRequestService(
            userRepository = FakeUserRepository(vendorUser),
            vendorRepository = FakeVendorRepository(vendor),
            vendorProductRepository = FakeVendorProductRepository(product),
            contractRequestRepository = contractRequestRepository,
        )

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createCommand(userId = vendorUser.id, productId = product.id))
        }

        assertEquals(ContractRequestErrorCode.PRODUCT_ALREADY_LOCKED, exception.errorCode)
    }

    @Test
    fun `update 시 자기 계약 요청에 묶인 배송 품목은 유지할 수 있다`() {
        val vendorUser = user(role = UserRole.VENDOR)
        val vendor = vendor(userId = vendorUser.id)
        val product = vendorProduct(vendorId = vendor.id)
        val contractRequest = contractRequest(vendorId = vendor.id, productId = product.id)
        val service = contractRequestService(
            userRepository = FakeUserRepository(vendorUser),
            vendorRepository = FakeVendorRepository(vendor),
            vendorProductRepository = FakeVendorProductRepository(product),
            contractRequestRepository = FakeContractRequestRepository(contractRequest),
            idGenerator = QueueIdGenerator(UUID.randomUUID(), UUID.randomUUID()),
        )

        val result = service.update(
            updateCommand(
                userId = vendorUser.id,
                contractRequestId = contractRequest.id,
                productId = product.id,
            )
        )

        assertEquals(contractRequest.id, result.contractRequestId)
        assertEquals(product.id, result.productId)
    }

    @Test
    fun `내 계약 요청 목록 조회 시 배송 품목 조건으로 필터링한다`() {
        val vendorUser = user(role = UserRole.VENDOR)
        val vendor = vendor(userId = vendorUser.id)
        val clothingRequest = contractRequest(
            vendorId = vendor.id,
            productId = null,
            productName = "대표 품목",
            items = listOf(contractRequestItem(productId = null, productName = "냉동 식품", coldChainType = ColdChainType.FROZEN)),
        )
        val normalRequest = contractRequest(
            vendorId = vendor.id,
            productId = null,
            productName = "상온 의류",
        )
        val service = contractRequestService(
            userRepository = FakeUserRepository(vendorUser),
            vendorRepository = FakeVendorRepository(vendor),
            contractRequestRepository = FakeContractRequestRepository(clothingRequest, normalRequest),
        )

        val results = service.getMyContractRequests(
            GetMyContractRequestsCommand(
                userId = vendorUser.id,
                productName = "냉동",
                productCategory = null,
                boxSize = null,
                coldChainType = ColdChainType.FROZEN,
                status = ContractRequestStatus.OPEN,
                pickupRegion = null,
                saturdayDeliveryRequired = null,
                returnRequired = null,
            ),
            Pageable.ofSize(20),
        )

        assertEquals(1, results.totalElements)
        assertEquals(clothingRequest.id, results.content.first().contractRequestId)
    }

    private fun contractRequestService(
        userRepository: UserRepository = FakeUserRepository(),
        vendorRepository: VendorRepository = FakeVendorRepository(),
        agencyRepository: AgencyRepository = FakeAgencyRepository(),
        vendorProductRepository: VendorProductRepository = FakeVendorProductRepository(),
        contractRequestRepository: ContractRequestRepository = FakeContractRequestRepository(),
        proposalRepository: ProposalRepository = FakeProposalRepository(),
        contractRepository: ContractRepository = FakeContractRepository(),
        idGenerator: IdGenerator = QueueIdGenerator(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
    ): ContractRequestService {
        return ContractRequestService(
            userRepository = userRepository,
            vendorRepository = vendorRepository,
            agencyRepository = agencyRepository,
            vendorProductRepository = vendorProductRepository,
            contractRequestRepository = contractRequestRepository,
            proposalRepository = proposalRepository,
            contractRepository = contractRepository,
            idGenerator = idGenerator,
        )
    }

    private fun createCommand(
        userId: UUID,
        productId: UUID?,
    ): CreateContractRequestCommand {
        return CreateContractRequestCommand(
            userId = userId,
            type = ContractRequestType.VENDOR_OFFER,
            approverId = null,
            productId = productId,
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
            items = listOf(itemCommand(productId = productId)),
        )
    }

    private fun updateCommand(
        userId: UUID,
        contractRequestId: UUID,
        productId: UUID?,
    ): UpdateContractRequestCommand {
        return UpdateContractRequestCommand(
            userId = userId,
            contractRequestId = contractRequestId,
            productId = productId,
            pickupRegion = "경기도 안산시 본오동",
            pickupAddress = "경기도 안산시 상록구 본오동 202호",
            monthlyVolume = 12,
            productCategory = ProductCategory.CLOTHING,
            productName = "의류",
            boxSize = BoxSize.SIZE_80,
            pickupStartTime = "10:00",
            pickupEndTime = "17:00",
            saturdayDeliveryRequired = false,
            returnRequired = true,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = BigDecimal("2100"),
            memo = "수정",
            items = listOf(itemCommand(productId = productId, boxSize = BoxSize.SIZE_80, boxQuantity = 12)),
        )
    }

    private fun itemCommand(
        productId: UUID?,
        boxSize: BoxSize = BoxSize.SIZE_60,
        boxQuantity: Int = 10,
    ): ContractRequestItemCommand {
        return ContractRequestItemCommand(
            productId = productId,
            productCategory = ProductCategory.CLOTHING,
            productName = "의류",
            boxSize = boxSize,
            boxQuantity = boxQuantity,
            itemQuantity = 0,
            averageWeightGram = 700,
            fragile = false,
            liquid = false,
            freshFood = false,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = BigDecimal("2000"),
        )
    }

    private fun contractRequest(
        id: UUID = UUID.randomUUID(),
        vendorId: UUID,
        productId: UUID?,
        status: ContractRequestStatus = ContractRequestStatus.OPEN,
        productName: String = "의류",
        items: List<ContractRequestItem> = listOf(contractRequestItem(productId = productId)),
    ): ContractRequest {
        return ContractRequest.create(
            id = id,
            type = ContractRequestType.VENDOR_OFFER,
            requesterId = vendorId,
            approverId = null,
            productId = productId,
            pickupRegion = "경기도 안산시 일동",
            pickupAddress = "경기도 안산시 상록구 일동 101호",
            monthlyVolume = 10,
            productCategory = ProductCategory.CLOTHING,
            productName = productName,
            boxSize = BoxSize.SIZE_60,
            pickupStartTime = "09:00",
            pickupEndTime = "18:00",
            saturdayDeliveryRequired = true,
            returnRequired = true,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = BigDecimal("2000"),
            memo = null,
            items = items,
            status = status,
        )
    }

    private fun contractRequestItem(
        productId: UUID?,
        productName: String = "의류",
        boxSize: BoxSize = BoxSize.SIZE_60,
        coldChainType: ColdChainType = ColdChainType.NONE,
    ): ContractRequestItem {
        return ContractRequestItem.create(
            id = UUID.randomUUID(),
            productId = productId,
            productCategory = ProductCategory.CLOTHING,
            productName = productName,
            boxSize = boxSize,
            boxQuantity = 10,
            itemQuantity = 0,
            averageWeightGram = 700,
            fragile = false,
            liquid = false,
            freshFood = false,
            coldChainType = coldChainType,
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

    private fun vendor(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
    ): Vendor {
        return Vendor.create(
            id = id,
            userId = userId,
            businessName = "안산 옷가게",
            businessRegistrationNumber = "123-45-67890",
            representativeName = "김사장",
            phoneNumber = "010-1234-5678",
            postalCode = "15360",
            address = "경기도 안산시 상록구 일동",
            addressDetail = "101호",
            mainRegion = "경기도 안산시 일동",
        )
    }

    private fun vendorProduct(
        id: UUID = UUID.randomUUID(),
        vendorId: UUID,
    ): VendorProduct {
        return VendorProduct.create(
            id = id,
            vendorId = vendorId,
            category = ProductCategory.CLOTHING,
            name = "의류",
            description = "일반 의류",
            averagePrice = BigDecimal("25000"),
            averageWeightGram = 700,
            boxSize = BoxSize.SIZE_60,
            boxQuantity = 10,
            itemQuantity = 0,
            destinationPostalCode = "06164",
            destinationAddress = "서울특별시 강남구 테헤란로 521",
            destinationAddressDetail = "10층",
            fragile = false,
            liquid = false,
            freshFood = false,
            coldChainType = ColdChainType.NONE,
        )
    }

    private class FakeUserRepository(
        vararg seedUsers: User,
    ) : UserRepository {
        private val users = seedUsers.associateBy { it.id }.toMutableMap()

        override fun findById(id: UUID): User? = users[id]
        override fun findByLoginId(loginId: String): User? = users.values.firstOrNull { it.loginId == loginId }
        override fun findByNameAndEmail(name: String, email: String): User? = users.values.firstOrNull { it.name == name && it.email == email }
        override fun findByLoginIdAndEmail(loginId: String, email: String): User? = users.values.firstOrNull { it.loginId == loginId && it.email == email }
        override fun existsByLoginId(loginId: String): Boolean = users.values.any { it.loginId == loginId }
        override fun existsByEmail(email: String): Boolean = users.values.any { it.email == email }
        override fun save(user: User): User {
            users[user.id] = user
            return user
        }
        override fun updatePassword(id: UUID, encodedPassword: String): User? {
            val user = users[id] ?: return null
            val changed = user.changePassword(encodedPassword)
            users[id] = changed
            return changed
        }
    }

    private class FakeVendorRepository(
        vararg seedVendors: Vendor,
    ) : VendorRepository {
        private val vendors = seedVendors.associateBy { it.id }.toMutableMap()

        override fun save(vendor: Vendor): Vendor {
            vendors[vendor.id] = vendor
            return vendor
        }
        override fun findById(id: UUID): Vendor? = vendors[id]
        override fun findAllByIds(ids: Collection<UUID>): List<Vendor> = vendors.values.filter { it.id in ids }
        override fun findByUserId(userId: UUID): Vendor? = vendors.values.firstOrNull { it.userId == userId }
        override fun existsByUserId(userId: UUID): Boolean = vendors.values.any { it.userId == userId }
    }

    private class FakeAgencyRepository : AgencyRepository {
        override fun save(agency: Agency): Agency = agency
        override fun findById(id: UUID): Agency? = null
        override fun findAllByIds(ids: Collection<UUID>): List<Agency> = emptyList()
        override fun findAll(condition: AgencySearchCondition, pageable: Pageable): Page<Agency> = PageImpl(emptyList(), pageable, 0)
        override fun findByUserId(userId: UUID): Agency? = null
        override fun existsByUserId(userId: UUID): Boolean = false
    }

    private class FakeVendorProductRepository(
        vararg seedProducts: VendorProduct,
    ) : VendorProductRepository {
        private val products = seedProducts.associateBy { it.id }.toMutableMap()

        override fun save(product: VendorProduct): VendorProduct {
            products[product.id] = product
            return product
        }
        override fun findByIdAndVendorId(id: UUID, vendorId: UUID): VendorProduct? = products[id]?.takeIf { it.vendorId == vendorId }
        override fun findAllByVendorId(vendorId: UUID, condition: VendorProductSearchCondition, pageable: Pageable): Page<VendorProduct> {
            val content = products.values.filter { it.vendorId == vendorId }
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun findAllByIdsAndVendorIdForUpdate(ids: Collection<UUID>, vendorId: UUID): List<VendorProduct> {
            return products.values.filter { it.id in ids && it.vendorId == vendorId }
        }
    }

    private class FakeContractRequestRepository(
        vararg seedRequests: ContractRequest,
    ) : ContractRequestRepository {
        private val requests = seedRequests.associateBy { it.id }.toMutableMap()

        override fun save(contractRequest: ContractRequest): ContractRequest {
            requests[contractRequest.id] = contractRequest
            return contractRequest
        }
        override fun findById(id: UUID): ContractRequest? = requests[id]
        override fun findByIdForUpdate(id: UUID): ContractRequest? = requests[id]
        override fun findByIdAndRequesterForUpdate(id: UUID, requesterType: ContractPartyType, requesterId: UUID): ContractRequest? {
            return requests[id]?.takeIf { it.requesterType == requesterType && it.requesterId == requesterId }
        }
        override fun findByIdAndApproverForUpdate(id: UUID, approverType: ContractPartyType, approverId: UUID): ContractRequest? {
            return requests[id]?.takeIf { it.approverType == approverType && it.approverId == approverId }
        }
        override fun findByIdAndVendorId(id: UUID, vendorId: UUID): ContractRequest? = requests[id]?.takeIf { it.vendorId == vendorId }
        override fun findByIdAndVendorIdForUpdate(id: UUID, vendorId: UUID): ContractRequest? = findByIdAndVendorId(id, vendorId)
        override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<ContractRequest> = PageImpl(requests.values.filter { it.vendorId == vendorId }, pageable, requests.size.toLong())
        override fun findAllByRequester(
            requesterType: ContractPartyType,
            requesterId: UUID,
            condition: ContractRequestSearchCondition,
            pageable: Pageable,
        ): Page<ContractRequest> {
            val content = requests.values.filter { request ->
                request.requesterType == requesterType &&
                    request.requesterId == requesterId &&
                    (condition.status == null || request.status == condition.status) &&
                    (condition.normalizedPickupRegion == null || request.pickupRegion.contains(condition.normalizedPickupRegion, ignoreCase = true)) &&
                    (condition.saturdayDeliveryRequired == null || request.saturdayDeliveryRequired == condition.saturdayDeliveryRequired) &&
                    (condition.returnRequired == null || request.returnRequired == condition.returnRequired) &&
                    (condition.normalizedProductName == null || request.productName.contains(condition.normalizedProductName, ignoreCase = true) || request.items.any { it.productName.contains(condition.normalizedProductName, ignoreCase = true) }) &&
                    (condition.productCategory == null || request.productCategory == condition.productCategory || request.items.any { it.productCategory == condition.productCategory }) &&
                    (condition.boxSize == null || request.boxSize == condition.boxSize || request.items.any { it.boxSize == condition.boxSize }) &&
                    (condition.coldChainType == null || request.coldChainType == condition.coldChainType || request.items.any { it.coldChainType == condition.coldChainType })
            }
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun findAllByApprover(approverType: ContractPartyType, approverId: UUID, pageable: Pageable): Page<ContractRequest> = PageImpl(requests.values.filter { it.approverType == approverType && it.approverId == approverId }, pageable, requests.size.toLong())
        override fun findAllByStatus(status: ContractRequestStatus, pageable: Pageable): Page<ContractRequest> = PageImpl(requests.values.filter { it.status == status }, pageable, requests.size.toLong())
        override fun findOpenVendorOffersForAgency(
            agencyId: UUID,
            condition: ContractRequestSearchCondition,
            pageable: Pageable,
        ): Page<ContractRequest> = PageImpl(emptyList(), pageable, 0)
        override fun findOpenVendorOffers(
            condition: ContractRequestSearchCondition,
            pageable: Pageable,
        ): Page<ContractRequest> = findAllByStatus(ContractRequestStatus.OPEN, pageable)
        override fun existsActiveByVendorIdAndProductIds(vendorId: UUID, productIds: Collection<UUID>, excludedContractRequestId: UUID?): Boolean {
            return requests.values.any { request ->
                request.id != excludedContractRequestId &&
                    request.vendorId == vendorId &&
                    request.status in setOf(ContractRequestStatus.OPEN, ContractRequestStatus.CONTRACTED) &&
                    (request.productId in productIds || request.items.any { it.productId in productIds })
            }
        }
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

    private class FakeContractRepository : ContractRepository {
        override fun save(contract: Contract): Contract = contract
        override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<Contract> = PageImpl(emptyList(), pageable, 0)
        override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Contract> = PageImpl(emptyList(), pageable, 0)
        override fun existsByContractRequestId(contractRequestId: UUID): Boolean = false
    }

    private class QueueIdGenerator(
        vararg ids: UUID,
    ) : IdGenerator {
        private val ids = ArrayDeque(ids.toList())

        override fun generate(): UUID {
            return ids.removeFirst()
        }
    }
}
