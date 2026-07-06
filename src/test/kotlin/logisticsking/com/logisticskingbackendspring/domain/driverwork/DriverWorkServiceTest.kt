package logisticsking.com.logisticskingbackendspring.domain.driverwork

import logisticsking.com.logisticskingbackendspring.app.driverwork.command.ApplyDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.CreateDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.SelectDriverWorkApplicationCommand
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractItem
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRepository
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestContractType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractStatus
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverEmploymentType
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverRepository
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.notification.Notification
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationPublisher
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationReferenceType
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationType
import logisticsking.com.logisticskingbackendspring.domain.notification.PublishNotificationCommand
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class DriverWorkServiceTest {

    @Test
    fun `create 성공 시 assignedDeliverId 없으면 OPEN 일감으로 생성한다`() {
        val fixture = fixture()

        val result = fixture.service.create(createCommand(fixture.agencyUser.id, fixture.contract.id))

        assertEquals(DriverWorkStatus.OPEN, result.status)
        assertEquals(null, result.assignedDeliverId)
    }

    @Test
    fun `create 성공 시 assignedDeliverId가 있으면 직접 할당 일감으로 생성한다`() {
        val fixture = fixture()

        val result = fixture.service.create(
            createCommand(
                userId = fixture.agencyUser.id,
                contractId = fixture.contract.id,
                assignedDeliverId = fixture.deliver.id,
            )
        )

        assertEquals(DriverWorkStatus.ASSIGNED, result.status)
        assertEquals(fixture.deliver.id, result.assignedDeliverId)
    }

    @Test
    fun `apply 성공 시 소속 기사가 OPEN 일감에 신청한다`() {
        val fixture = fixture()
        val work = fixture.service.create(createCommand(fixture.agencyUser.id, fixture.contract.id))

        val result = fixture.service.apply(
            ApplyDriverWorkCommand(
                userId = fixture.driverUser.id,
                driverWorkId = work.driverWorkId,
                memo = "가능합니다.",
            )
        )

        assertEquals(DriverWorkApplicationStatus.APPLIED, result.status)
        assertEquals(fixture.deliver.id, result.deliverId)
    }

    @Test
    fun `select 성공 시 신청자를 선택하고 일감을 할당한다`() {
        val fixture = fixture()
        val work = fixture.service.create(createCommand(fixture.agencyUser.id, fixture.contract.id))
        val application = fixture.service.apply(
            ApplyDriverWorkCommand(
                userId = fixture.driverUser.id,
                driverWorkId = work.driverWorkId,
                memo = null,
            )
        )

        val result = fixture.service.select(
            SelectDriverWorkApplicationCommand(
                userId = fixture.agencyUser.id,
                driverWorkId = work.driverWorkId,
                applicationId = application.applicationId,
            )
        )

        assertEquals(DriverWorkStatus.ASSIGNED, result.status)
        assertEquals(fixture.deliver.id, result.assignedDeliverId)
        val selected = fixture.applicationRepository.findByIdAndDriverWorkId(
            id = application.applicationId,
            driverWorkId = work.driverWorkId,
        )
        assertEquals(DriverWorkApplicationStatus.SELECTED, selected?.status)
    }

    @Test
    fun `apply 시 프리랜서 기사는 소속 일감에 신청할 수 없다`() {
        val fixture = fixture()
        val freelancerUser = user(UserRole.DRIVER)
        val freelancer = deliver(
            userId = freelancerUser.id,
            agencyId = null,
            employmentType = DeliverEmploymentType.FREELANCER,
        )
        fixture.userRepository.save(freelancerUser)
        fixture.deliverRepository.save(freelancer)
        val work = fixture.service.create(createCommand(fixture.agencyUser.id, fixture.contract.id))

        val exception = assertThrows(GlobalException::class.java) {
            fixture.service.apply(
                ApplyDriverWorkCommand(
                    userId = freelancerUser.id,
                    driverWorkId = work.driverWorkId,
                    memo = null,
                )
            )
        }

        assertEquals(DriverWorkErrorCode.DELIVER_IS_NOT_AGENCY_AFFILIATED, exception.errorCode)
    }

    private fun fixture(): Fixture {
        val agencyUser = user(UserRole.AGENCY)
        val driverUser = user(UserRole.DRIVER)
        val agency = agency(userId = agencyUser.id)
        val deliver = deliver(
            userId = driverUser.id,
            agencyId = agency.id,
            employmentType = DeliverEmploymentType.AGENCY_AFFILIATED,
        )
        val contract = contract(agencyId = agency.id)
        val userRepository = FakeUserRepository(listOf(agencyUser, driverUser))
        val agencyRepository = FakeAgencyRepository(listOf(agency))
        val contractRepository = FakeContractRepository(listOf(contract))
        val deliverRepository = FakeDeliverRepository(listOf(deliver))
        val workRepository = FakeDriverWorkRepository()
        val applicationRepository = FakeDriverWorkApplicationRepository()
        val service = DriverWorkService(
            userRepository = userRepository,
            agencyRepository = agencyRepository,
            contractRepository = contractRepository,
            deliverRepository = deliverRepository,
            driverWorkRepository = workRepository,
            driverWorkApplicationRepository = applicationRepository,
            notificationPublisher = FakeNotificationPublisher(),
            idGenerator = QueueIdGenerator(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
            ),
        )

        return Fixture(
            service = service,
            userRepository = userRepository,
            deliverRepository = deliverRepository,
            applicationRepository = applicationRepository,
            agencyUser = agencyUser,
            driverUser = driverUser,
            agency = agency,
            deliver = deliver,
            contract = contract,
        )
    }

    private fun createCommand(
        userId: UUID,
        contractId: UUID,
        assignedDeliverId: UUID? = null,
    ): CreateDriverWorkCommand {
        return CreateDriverWorkCommand(
            userId = userId,
            contractId = contractId,
            title = "유성구 봉명동 집하",
            serviceRegion = "대전 유성구 봉명동",
            pickupStartDate = LocalDate.of(2026, 7, 10),
            pickupEndDate = LocalDate.of(2026, 7, 31),
            expectedVolume = 500,
            unitPrice = BigDecimal("900"),
            assignedDeliverId = assignedDeliverId,
            memo = "오후 집하 중심",
        )
    }

    private fun user(role: UserRole): User {
        return User.create(
            id = UUID.randomUUID(),
            loginId = "user-${UUID.randomUUID()}",
            email = "${UUID.randomUUID()}@example.com",
            encodedPassword = "encoded-password",
            name = "user",
            role = role,
        )
    }

    private fun agency(userId: UUID): Agency {
        return Agency.create(
            id = UUID.randomUUID(),
            userId = userId,
            carrier = Carrier.CJ,
            agencyName = "CJ 유성대리점",
            businessRegistrationNumber = "123-45-67890",
            representativeName = "김대표",
            phoneNumber = "010-1234-5678",
            postalCode = "34100",
            address = "대전 유성구 봉명동",
            addressDetail = "1층",
            mainRegion = "대전 유성구",
            serviceRegions = listOf("대전 유성구"),
            weekdayPickupStartTime = "09:00",
            weekdayPickupEndTime = "18:00",
            saturdayPickupAvailable = true,
            saturdayDeliveryAvailable = true,
            returnAvailable = true,
            supportedColdChainTypes = setOf(ColdChainType.NONE),
            maxMonthlyVolume = 10000,
        )
    }

    private fun deliver(
        userId: UUID,
        agencyId: UUID?,
        employmentType: DeliverEmploymentType,
    ): Deliver {
        return Deliver.create(
            id = UUID.randomUUID(),
            userId = userId,
            employmentType = employmentType,
            agencyId = agencyId,
            driverName = "김기사",
            phoneNumber = "010-1111-2222",
            vehicleNumber = "12가3456",
            serviceRegions = listOf("대전 유성구"),
            active = true,
            memo = null,
        )
    }

    private fun contract(agencyId: UUID): Contract {
        return Contract.restore(
            id = UUID.randomUUID(),
            contractRequestId = UUID.randomUUID(),
            proposalId = UUID.randomUUID(),
            vendorId = UUID.randomUUID(),
            agencyId = agencyId,
            pickupRegion = "대전 유성구",
            pickupAddress = "대전 유성구 봉명동",
            contractType = ContractRequestContractType.SINGLE,
            pickupDateFrom = LocalDate.of(2026, 7, 10),
            pickupDateTo = LocalDate.of(2026, 7, 31),
            deliveryDateFrom = null,
            deliveryDateTo = null,
            recurringPickupCycle = null,
            recurringPickupDaysOfWeek = emptyList(),
            recurringPickupDayOfMonth = null,
            monthlyVolume = 500,
            productCategory = ProductCategory.CLOTHING,
            productName = "의류",
            boxSize = BoxSize.SIZE_80,
            unitPrice = BigDecimal("2500"),
            pickupStartTime = "09:00",
            pickupEndTime = "18:00",
            saturdayDeliveryAvailable = true,
            returnAvailable = true,
            coldChainType = ColdChainType.NONE,
            memo = null,
            items = listOf(
                ContractItem(
                    id = UUID.randomUUID(),
                    productId = UUID.randomUUID(),
                    productCategory = ProductCategory.CLOTHING,
                    productName = "의류",
                    boxSize = BoxSize.SIZE_80,
                    boxQuantity = 10,
                    itemQuantity = 10,
                    averageWeightGram = null,
                    fragile = false,
                    liquid = false,
                    freshFood = false,
                    coldChainType = ColdChainType.NONE,
                    unitPrice = BigDecimal("2500"),
                )
            ),
            status = ContractStatus.ACTIVE,
        )
    }

    private data class Fixture(
        val service: DriverWorkService,
        val userRepository: FakeUserRepository,
        val deliverRepository: FakeDeliverRepository,
        val applicationRepository: FakeDriverWorkApplicationRepository,
        val agencyUser: User,
        val driverUser: User,
        val agency: Agency,
        val deliver: Deliver,
        val contract: Contract,
    )

    private class FakeUserRepository(users: List<User>) : UserRepository {
        private val users = users.associateBy(User::id).toMutableMap()
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
        override fun updatePassword(id: UUID, encodedPassword: String): User? = users[id]?.changePassword(encodedPassword)
    }

    private class FakeAgencyRepository(agencies: List<Agency>) : AgencyRepository {
        private val agencies = agencies.associateBy(Agency::id)
        override fun save(agency: Agency): Agency = agency
        override fun findById(id: UUID): Agency? = agencies[id]
        override fun findAllByIds(ids: Collection<UUID>): List<Agency> = agencies.values.filter { it.id in ids }
        override fun findAll(condition: AgencySearchCondition, pageable: Pageable): Page<Agency> = PageImpl(agencies.values.toList(), pageable, agencies.size.toLong())
        override fun findAllForRecommendation(): List<Agency> = agencies.values.toList()
        override fun findByUserId(userId: UUID): Agency? = agencies.values.firstOrNull { it.userId == userId }
        override fun existsByUserId(userId: UUID): Boolean = agencies.values.any { it.userId == userId }
    }

    private class FakeContractRepository(contracts: List<Contract>) : ContractRepository {
        private val contracts = contracts.associateBy(Contract::id)
        override fun save(contract: Contract): Contract = contract
        override fun findById(id: UUID): Contract? = contracts[id]
        override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<Contract> = PageImpl(emptyList(), pageable, 0)
        override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Contract> = PageImpl(emptyList(), pageable, 0)
        override fun findRecentAgencyIdsByVendorId(vendorId: UUID, limit: Int): List<UUID> = emptyList()
        override fun findRecentVendorIdsByAgencyId(agencyId: UUID, limit: Int): List<UUID> = emptyList()
        override fun existsByContractRequestId(contractRequestId: UUID): Boolean = false
    }

    private class FakeDeliverRepository(delivers: List<Deliver>) : DeliverRepository {
        private val delivers = delivers.associateBy(Deliver::id).toMutableMap()
        override fun save(deliver: Deliver): Deliver {
            delivers[deliver.id] = deliver
            return deliver
        }
        override fun findById(id: UUID): Deliver? = delivers[id]
        override fun findAllByIds(ids: Collection<UUID>): List<Deliver> = delivers.values.filter { it.id in ids }
        override fun findByUserId(userId: UUID): Deliver? = delivers.values.firstOrNull { it.userId == userId }
        override fun findAllByAgencyId(agencyId: UUID, condition: DeliverSearchCondition, pageable: Pageable): Page<Deliver> {
            val content = delivers.values.filter { it.agencyId == agencyId }
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun findAllFreelancers(condition: DeliverSearchCondition, pageable: Pageable): Page<Deliver> {
            val content = delivers.values.filter { it.employmentType == DeliverEmploymentType.FREELANCER }
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun existsByUserId(userId: UUID): Boolean = delivers.values.any { it.userId == userId }
    }

    private class FakeDriverWorkRepository : DriverWorkRepository {
        private val works = mutableMapOf<UUID, DriverWork>()
        override fun save(driverWork: DriverWork): DriverWork {
            works[driverWork.id] = driverWork
            return driverWork
        }
        override fun findById(id: UUID): DriverWork? = works[id]
        override fun findByIdAndAgencyId(id: UUID, agencyId: UUID): DriverWork? = works[id]?.takeIf { it.agencyId == agencyId }
        override fun findAllByAgencyId(agencyId: UUID, condition: DriverWorkSearchCondition, pageable: Pageable): Page<DriverWork> {
            val content = works.values.filter { it.agencyId == agencyId }
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun findOpenByAgencyId(agencyId: UUID, condition: DriverWorkSearchCondition, pageable: Pageable): Page<DriverWork> {
            val content = works.values.filter { it.agencyId == agencyId && it.status == DriverWorkStatus.OPEN }
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun findAssignedByDeliverId(deliverId: UUID, condition: DriverWorkSearchCondition, pageable: Pageable): Page<DriverWork> {
            val content = works.values.filter { it.assignedDeliverId == deliverId }
            return PageImpl(content, pageable, content.size.toLong())
        }
    }

    private class FakeDriverWorkApplicationRepository : DriverWorkApplicationRepository {
        private val applications = mutableMapOf<UUID, DriverWorkApplication>()
        override fun save(application: DriverWorkApplication): DriverWorkApplication {
            applications[application.id] = application
            return application
        }
        override fun saveAll(applications: List<DriverWorkApplication>): List<DriverWorkApplication> {
            applications.forEach { this.applications[it.id] = it }
            return applications
        }
        override fun findByIdAndDriverWorkId(id: UUID, driverWorkId: UUID): DriverWorkApplication? {
            return applications[id]?.takeIf { it.driverWorkId == driverWorkId }
        }
        override fun findByDriverWorkIdAndDeliverId(driverWorkId: UUID, deliverId: UUID): DriverWorkApplication? {
            return applications.values.firstOrNull { it.driverWorkId == driverWorkId && it.deliverId == deliverId }
        }
        override fun findAllByDriverWorkId(driverWorkId: UUID): List<DriverWorkApplication> {
            return applications.values.filter { it.driverWorkId == driverWorkId }
        }
        override fun findAllByDriverWorkId(driverWorkId: UUID, pageable: Pageable): Page<DriverWorkApplication> {
            val content = findAllByDriverWorkId(driverWorkId)
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun findAllByDeliverId(deliverId: UUID, pageable: Pageable): Page<DriverWorkApplication> {
            val content = applications.values.filter { it.deliverId == deliverId }
            return PageImpl(content, pageable, content.size.toLong())
        }
        override fun existsAppliedByDriverWorkIdAndDeliverId(driverWorkId: UUID, deliverId: UUID): Boolean {
            return applications.values.any {
                it.driverWorkId == driverWorkId &&
                    it.deliverId == deliverId &&
                    it.status == DriverWorkApplicationStatus.APPLIED
            }
        }
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
                title = "title",
                message = "message",
                linkUrl = linkUrl,
                referenceType = referenceType,
                referenceId = referenceId,
            )
        }

        override fun publishAll(commands: List<PublishNotificationCommand>): List<Notification> = emptyList()
    }

    private class QueueIdGenerator(
        vararg ids: UUID,
    ) : IdGenerator {
        private val ids = ArrayDeque(ids.toList())
        override fun generate(): UUID = ids.removeFirst()
    }
}
