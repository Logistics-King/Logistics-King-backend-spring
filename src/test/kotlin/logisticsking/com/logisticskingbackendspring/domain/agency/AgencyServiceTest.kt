package logisticsking.com.logisticskingbackendspring.domain.agency

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.app.agency.command.CreateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.command.UpdateAgencyCommand
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Pageable.unpaged
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class AgencyServiceTest {

    @Test
    fun `create 성공 시 대리점 프로필을 저장한다`() {
        val user = user(role = UserRole.AGENCY)
        val agencyId = UUID.randomUUID()
        val service = agencyService(
            userRepository = FakeUserRepository(user),
            idGenerator = FakeIdGenerator(agencyId),
        )

        val result = service.create(createAgencyCommand(user.id))

        assertEquals(agencyId, result.agencyId)
        assertEquals(user.id, result.userId)
        assertEquals(Carrier.CJ, result.carrier)
        assertEquals("CJ 일동대리점", result.agencyName)
    }

    @Test
    fun `create 시 대리점 권한이 아니면 예외가 발생한다`() {
        val user = user(role = UserRole.VENDOR)
        val service = agencyService(userRepository = FakeUserRepository(user))

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createAgencyCommand(user.id))
        }

        assertEquals(AgencyErrorCode.USER_IS_NOT_AGENCY, exception.errorCode)
    }

    @Test
    fun `create 시 이미 대리점 프로필이 있으면 예외가 발생한다`() {
        val user = user(role = UserRole.AGENCY)
        val agencyRepository = FakeAgencyRepository()
        val service = agencyService(
            userRepository = FakeUserRepository(user),
            agencyRepository = agencyRepository,
        )
        service.create(createAgencyCommand(user.id))

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createAgencyCommand(user.id))
        }

        assertEquals(AgencyErrorCode.AGENCY_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `getMyAgency 성공 시 내 대리점 프로필을 조회한다`() {
        val user = user(role = UserRole.AGENCY)
        val agencyRepository = FakeAgencyRepository()
        val service = agencyService(
            userRepository = FakeUserRepository(user),
            agencyRepository = agencyRepository,
        )
        val created = service.create(createAgencyCommand(user.id))

        val result = service.getMyAgency(user.id)

        assertEquals(created.agencyId, result.agencyId)
    }

    @Test
    fun `getAgencies 성공 시 화주가 지역으로 근방 대리점 목록을 조회한다`() {
        val vendorUser = user(role = UserRole.VENDOR)
        val agencyRepository = FakeAgencyRepository()
        val service = agencyService(
            userRepository = FakeUserRepository(vendorUser),
            agencyRepository = agencyRepository,
        )
        val matched = agency(name = "CJ 일동대리점", mainRegion = "경기도 안산시 일동")
        val unmatched = agency(name = "롯데 선부대리점", mainRegion = "경기도 안산시 선부동")
        agencyRepository.save(matched)
        agencyRepository.save(unmatched)

        val result = service.getAgencies(
            userId = vendorUser.id,
            condition = AgencySearchCondition(region = "일동"),
            pageable = unpaged(),
        )

        assertEquals(1, result.totalElements)
        assertEquals(matched.id, result.content.first().agencyId)
    }

    @Test
    fun `getAgency 성공 시 화주가 대리점 상세를 조회한다`() {
        val vendorUser = user(role = UserRole.VENDOR)
        val agencyRepository = FakeAgencyRepository()
        val service = agencyService(
            userRepository = FakeUserRepository(vendorUser),
            agencyRepository = agencyRepository,
        )
        val agency = agency(name = "한진 사동대리점", mainRegion = "경기도 안산시 사동")
        agencyRepository.save(agency)

        val result = service.getAgency(
            userId = vendorUser.id,
            agencyId = agency.id,
        )

        assertEquals(agency.id, result.agencyId)
        assertEquals("한진 사동대리점", result.agencyName)
    }

    @Test
    fun `getAgencies 시 화주 권한이 아니면 예외가 발생한다`() {
        val agencyUser = user(role = UserRole.AGENCY)
        val service = agencyService(userRepository = FakeUserRepository(agencyUser))

        val exception = assertThrows(GlobalException::class.java) {
            service.getAgencies(
                userId = agencyUser.id,
                condition = AgencySearchCondition(region = "일동"),
                pageable = unpaged(),
            )
        }

        assertEquals(AgencyErrorCode.USER_IS_NOT_VENDOR, exception.errorCode)
    }

    @Test
    fun `update 성공 시 대리점 프로필을 수정한다`() {
        val user = user(role = UserRole.AGENCY)
        val agencyRepository = FakeAgencyRepository()
        val service = agencyService(
            userRepository = FakeUserRepository(user),
            agencyRepository = agencyRepository,
        )
        service.create(createAgencyCommand(user.id))

        val result = service.update(updateAgencyCommand(user.id))

        assertEquals(Carrier.HANJIN, result.carrier)
        assertEquals("한진 사동대리점", result.agencyName)
        assertEquals(listOf("경기도 안산시 사동"), result.serviceRegions)
    }

    private fun agencyService(
        userRepository: FakeUserRepository = FakeUserRepository(user(role = UserRole.AGENCY)),
        agencyRepository: FakeAgencyRepository = FakeAgencyRepository(),
        vendorRepository: FakeVendorRepository = FakeVendorRepository(),
        idGenerator: IdGenerator = FakeIdGenerator(UUID.randomUUID()),
    ): AgencyService {
        return AgencyService(
            userRepository = userRepository,
            agencyRepository = agencyRepository,
            vendorRepository = vendorRepository,
            idGenerator = idGenerator,
        )
    }

    private fun createAgencyCommand(userId: UUID): CreateAgencyCommand {
        return CreateAgencyCommand(
            userId = userId,
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
            supportedColdChainTypes = setOf(ColdChainType.REFRIGERATED, ColdChainType.FROZEN),
            maxMonthlyVolume = 10000,
        )
    }

    private fun updateAgencyCommand(userId: UUID): UpdateAgencyCommand {
        return UpdateAgencyCommand(
            userId = userId,
            carrier = Carrier.HANJIN,
            agencyName = "한진 사동대리점",
            businessRegistrationNumber = "987-65-43210",
            representativeName = "박대표",
            phoneNumber = "010-9876-5432",
            postalCode = "15500",
            address = "경기도 안산시 상록구 사동",
            addressDetail = "2층",
            mainRegion = "경기도 안산시 사동",
            serviceRegions = listOf("경기도 안산시 사동"),
            weekdayPickupStartTime = "10:00",
            weekdayPickupEndTime = "17:00",
            saturdayPickupAvailable = false,
            saturdayDeliveryAvailable = false,
            returnAvailable = true,
            supportedColdChainTypes = setOf(ColdChainType.NONE),
            maxMonthlyVolume = 5000,
        )
    }

    private fun agency(
        name: String,
        mainRegion: String,
    ): Agency {
        return Agency.create(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            carrier = Carrier.CJ,
            agencyName = name,
            businessRegistrationNumber = "123-45-67890",
            representativeName = "김대표",
            phoneNumber = "010-1234-5678",
            postalCode = "15360",
            address = mainRegion,
            addressDetail = "1층",
            mainRegion = mainRegion,
            serviceRegions = listOf(mainRegion),
            weekdayPickupStartTime = "09:00",
            weekdayPickupEndTime = "18:00",
            saturdayPickupAvailable = true,
            saturdayDeliveryAvailable = true,
            returnAvailable = true,
            supportedColdChainTypes = setOf(ColdChainType.NONE),
            maxMonthlyVolume = 10000,
        )
    }

    private fun user(role: UserRole): User {
        return User.create(
            id = UUID.randomUUID(),
            loginId = "agency01",
            email = "agency01@example.com",
            encodedPassword = "encoded-password",
            name = "agency",
            role = role,
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

    private class FakeAgencyRepository : AgencyRepository {
        private val agencies = mutableMapOf<UUID, Agency>()

        override fun save(agency: Agency): Agency {
            agencies[agency.id] = agency
            return agency
        }

        override fun findById(id: UUID): Agency? {
            return agencies[id]
        }

        override fun findAllByIds(ids: Collection<UUID>): List<Agency> {
            return agencies.values.filter { it.id in ids }
        }

        override fun findAll(
            condition: AgencySearchCondition,
            pageable: Pageable,
        ): Page<Agency> {
            val content = agencies.values
                .filter { agency ->
                    condition.normalizedRegion == null ||
                        agency.mainRegion.contains(condition.normalizedRegion, ignoreCase = true) ||
                        agency.serviceRegions.any { it.contains(condition.normalizedRegion, ignoreCase = true) }
                }
                .filter { agency -> condition.carrier == null || agency.carrier == condition.carrier }
                .filter { agency ->
                    condition.saturdayDeliveryAvailable == null ||
                        agency.saturdayDeliveryAvailable == condition.saturdayDeliveryAvailable
                }
                .filter { agency -> condition.returnAvailable == null || agency.returnAvailable == condition.returnAvailable }

            return PageImpl(content, pageable, content.size.toLong())
        }

        override fun findAllForRecommendation(): List<Agency> {
            return agencies.values.toList()
        }

        override fun findByUserId(userId: UUID): Agency? {
            return agencies.values.firstOrNull { it.userId == userId }
        }

        override fun existsByUserId(userId: UUID): Boolean {
            return agencies.values.any { it.userId == userId }
        }
    }

    private class FakeVendorRepository : VendorRepository {
        private val vendors = mutableMapOf<UUID, Vendor>()

        override fun save(vendor: Vendor): Vendor {
            vendors[vendor.id] = vendor
            return vendor
        }

        override fun findById(id: UUID): Vendor? {
            return vendors[id]
        }

        override fun findAllByIds(ids: Collection<UUID>): List<Vendor> {
            return vendors.values.filter { it.id in ids }
        }

        override fun findByUserId(userId: UUID): Vendor? {
            return vendors.values.firstOrNull { it.userId == userId }
        }

        override fun existsByUserId(userId: UUID): Boolean {
            return vendors.values.any { it.userId == userId }
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
