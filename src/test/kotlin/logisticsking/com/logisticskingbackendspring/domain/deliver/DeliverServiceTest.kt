package logisticsking.com.logisticskingbackendspring.domain.deliver

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.app.deliver.command.CreateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.command.UpdateDeliverCommand
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.UUID

class DeliverServiceTest {

    @Test
    fun `create 성공 시 배송기사 프로필을 저장한다`() {
        val user = user(role = UserRole.DRIVER)
        val agency = agency()
        val deliverId = UUID.randomUUID()
        val service = deliverService(
            userRepository = FakeUserRepository(user),
            agencyRepository = FakeAgencyRepository(agency),
            idGenerator = FakeIdGenerator(deliverId),
        )

        val result = service.create(createDeliverCommand(user.id, agency.id))

        assertEquals(deliverId, result.deliverId)
        assertEquals(user.id, result.userId)
        assertEquals(agency.id, result.agencyId)
        assertEquals("김택배", result.driverName)
    }

    @Test
    fun `create 시 배송기사 권한이 아니면 예외가 발생한다`() {
        val user = user(role = UserRole.AGENCY)
        val service = deliverService(userRepository = FakeUserRepository(user))

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createDeliverCommand(user.id, UUID.randomUUID()))
        }

        assertEquals(DeliverErrorCode.USER_IS_NOT_DRIVER, exception.errorCode)
    }

    @Test
    fun `create 시 소속 대리점이 없으면 예외가 발생한다`() {
        val user = user(role = UserRole.DRIVER)
        val service = deliverService(userRepository = FakeUserRepository(user))

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createDeliverCommand(user.id, UUID.randomUUID()))
        }

        assertEquals(DeliverErrorCode.AGENCY_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `create 시 이미 배송기사 프로필이 있으면 예외가 발생한다`() {
        val user = user(role = UserRole.DRIVER)
        val agency = agency()
        val deliverRepository = FakeDeliverRepository()
        val service = deliverService(
            userRepository = FakeUserRepository(user),
            agencyRepository = FakeAgencyRepository(agency),
            deliverRepository = deliverRepository,
        )
        service.create(createDeliverCommand(user.id, agency.id))

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createDeliverCommand(user.id, agency.id))
        }

        assertEquals(DeliverErrorCode.DELIVER_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `getMyDeliver 성공 시 내 배송기사 프로필을 조회한다`() {
        val user = user(role = UserRole.DRIVER)
        val agency = agency()
        val deliverRepository = FakeDeliverRepository()
        val service = deliverService(
            userRepository = FakeUserRepository(user),
            agencyRepository = FakeAgencyRepository(agency),
            deliverRepository = deliverRepository,
        )
        val created = service.create(createDeliverCommand(user.id, agency.id))

        val result = service.getMyDeliver(user.id)

        assertEquals(created.deliverId, result.deliverId)
    }

    @Test
    fun `update 성공 시 배송기사 프로필을 수정한다`() {
        val user = user(role = UserRole.DRIVER)
        val agency = agency()
        val deliverRepository = FakeDeliverRepository()
        val service = deliverService(
            userRepository = FakeUserRepository(user),
            agencyRepository = FakeAgencyRepository(agency),
            deliverRepository = deliverRepository,
        )
        service.create(createDeliverCommand(user.id, agency.id))

        val result = service.update(updateDeliverCommand(user.id, agency.id))

        assertEquals("박배송", result.driverName)
        assertEquals("34나5678", result.vehicleNumber)
        assertEquals(listOf("경기도 안산시 사동"), result.serviceRegions)
        assertEquals(false, result.active)
    }

    private fun deliverService(
        userRepository: FakeUserRepository = FakeUserRepository(user(role = UserRole.DRIVER)),
        agencyRepository: FakeAgencyRepository = FakeAgencyRepository(agency()),
        deliverRepository: FakeDeliverRepository = FakeDeliverRepository(),
        idGenerator: IdGenerator = FakeIdGenerator(UUID.randomUUID()),
    ): DeliverService {
        return DeliverService(
            userRepository = userRepository,
            agencyRepository = agencyRepository,
            deliverRepository = deliverRepository,
            idGenerator = idGenerator,
        )
    }

    private fun createDeliverCommand(userId: UUID, agencyId: UUID): CreateDeliverCommand {
        return CreateDeliverCommand(
            userId = userId,
            agencyId = agencyId,
            driverName = "김택배",
            phoneNumber = "010-1234-5678",
            vehicleNumber = "12가3456",
            serviceRegions = listOf("경기도 안산시 일동", "경기도 안산시 본오동"),
            active = true,
            memo = "오전 집하 담당",
        )
    }

    private fun updateDeliverCommand(userId: UUID, agencyId: UUID): UpdateDeliverCommand {
        return UpdateDeliverCommand(
            userId = userId,
            agencyId = agencyId,
            driverName = "박배송",
            phoneNumber = "010-9876-5432",
            vehicleNumber = "34나5678",
            serviceRegions = listOf("경기도 안산시 사동"),
            active = false,
            memo = "오후 집하 담당",
        )
    }

    private fun user(role: UserRole): User {
        return User.create(
            id = UUID.randomUUID(),
            loginId = "driver01",
            email = "driver01@example.com",
            encodedPassword = "encoded-password",
            name = "driver",
            role = role,
        )
    }

    private fun agency(id: UUID = UUID.randomUUID()): Agency {
        return Agency.create(
            id = id,
            userId = UUID.randomUUID(),
            carrier = Carrier.CJ,
            agencyName = "CJ 일동대리점",
            businessRegistrationNumber = "123-45-67890",
            representativeName = "김대표",
            phoneNumber = "010-1234-5678",
            postalCode = "15360",
            address = "경기도 안산시 상록구 일동",
            addressDetail = "1층",
            mainRegion = "경기도 안산시 일동",
            serviceRegions = listOf("경기도 안산시 일동", "경기도 안산시 본오동"),
            weekdayPickupStartTime = "09:00",
            weekdayPickupEndTime = "18:00",
            saturdayPickupAvailable = true,
            saturdayDeliveryAvailable = true,
            returnAvailable = true,
            supportedColdChainTypes = setOf(ColdChainType.NONE),
            maxMonthlyVolume = 10000,
        )
    }

    private class FakeUserRepository(
        user: User? = null,
    ) : UserRepository {
        private val users = user
            ?.let { mutableMapOf(it.id to it) }
            ?: mutableMapOf()

        override fun findById(id: UUID): User? {
            return users[id]
        }

        override fun findByLoginId(loginId: String): User? {
            return users.values.firstOrNull { it.loginId == loginId }
        }

        override fun findByNameAndEmail(
            name: String,
            email: String,
        ): User? {
            return users.values.firstOrNull { it.name == name && it.email == email }
        }

        override fun findByLoginIdAndEmail(
            loginId: String,
            email: String,
        ): User? {
            return users.values.firstOrNull { it.loginId == loginId && it.email == email }
        }

        override fun existsByLoginId(loginId: String): Boolean {
            return users.values.any { it.loginId == loginId }
        }

        override fun existsByEmail(email: String): Boolean {
            return users.values.any { it.email == email }
        }

        override fun save(user: User): User {
            users[user.id] = user
            return user
        }

        override fun updatePassword(
            id: UUID,
            encodedPassword: String,
        ): User? {
            val user = users[id] ?: return null
            val changed = user.changePassword(encodedPassword)
            users[id] = changed
            return changed
        }
    }

    private class FakeAgencyRepository(
        agency: Agency? = null,
    ) : AgencyRepository {
        private val agencies = agency
            ?.let { mutableMapOf(it.id to it) }
            ?: mutableMapOf()

        override fun save(agency: Agency): Agency {
            agencies[agency.id] = agency
            return agency
        }

        override fun findById(id: UUID): Agency? {
            return agencies[id]
        }

        override fun findAll(
            condition: AgencySearchCondition,
            pageable: Pageable,
        ): Page<Agency> {
            return PageImpl(agencies.values.toList(), pageable, agencies.size.toLong())
        }

        override fun findByUserId(userId: UUID): Agency? {
            return agencies.values.firstOrNull { it.userId == userId }
        }

        override fun existsByUserId(userId: UUID): Boolean {
            return agencies.values.any { it.userId == userId }
        }
    }

    private class FakeDeliverRepository : DeliverRepository {
        private val delivers = mutableMapOf<UUID, Deliver>()

        override fun save(deliver: Deliver): Deliver {
            delivers[deliver.id] = deliver
            return deliver
        }

        override fun findById(id: UUID): Deliver? {
            return delivers[id]
        }

        override fun findByUserId(userId: UUID): Deliver? {
            return delivers.values.firstOrNull { it.userId == userId }
        }

        override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Deliver> {
            val filteredDelivers = delivers.values.filter { it.agencyId == agencyId }

            return PageImpl(filteredDelivers, pageable, filteredDelivers.size.toLong())
        }

        override fun existsByUserId(userId: UUID): Boolean {
            return delivers.values.any { it.userId == userId }
        }
    }

    private class FakeIdGenerator(
        private val id: UUID,
    ) : IdGenerator {
        override fun generate(): UUID {
            return id
        }
    }
}
