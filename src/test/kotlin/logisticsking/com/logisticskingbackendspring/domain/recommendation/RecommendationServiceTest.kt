package logisticsking.com.logisticskingbackendspring.domain.recommendation

import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRepository
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.UUID

class RecommendationServiceTest {

    @Test
    fun `화주에게 이전 계약과 지역 기준으로 대리점을 추천한다`() {
        val vendorUser = user(role = UserRole.VENDOR)
        val vendor = vendor(userId = vendorUser.id, mainRegion = "경기도 안산시 일동")
        val previousMatchedAgency = agency(
            name = "CJ 일동대리점",
            mainRegion = "경기도 안산시 일동",
            serviceRegions = listOf("경기도 안산시 일동", "경기도 안산시 본오동"),
        )
        val regionMatchedAgency = agency(
            name = "한진 본오대리점",
            mainRegion = "경기도 안산시 본오동",
            serviceRegions = listOf("경기도 안산시 일동"),
        )
        val unmatchedAgency = agency(
            name = "롯데 선부대리점",
            mainRegion = "경기도 안산시 선부동",
            serviceRegions = listOf("경기도 안산시 선부동"),
        )
        val service = recommendationService(
            userRepository = FakeUserRepository(vendorUser),
            vendorRepository = FakeVendorRepository(vendor),
            agencyRepository = FakeAgencyRepository(previousMatchedAgency, regionMatchedAgency, unmatchedAgency),
            contractRepository = FakeContractRepository(previousMatchedAgency.id),
        )

        val results = service.getRecommendedAgencies(
            userId = vendorUser.id,
            limit = 10,
        )

        assertEquals(2, results.size)
        assertEquals(previousMatchedAgency.id, results[0].agencyId)
        assertEquals(180, results[0].score)
        assertEquals(
            listOf(
                RecommendationReasonType.PREVIOUS_CONTRACT,
                RecommendationReasonType.SERVICE_REGION_MATCH,
                RecommendationReasonType.MAIN_REGION_MATCH,
            ),
            results[0].reasons.map { it.type },
        )
        assertEquals(regionMatchedAgency.id, results[1].agencyId)
        assertEquals(50, results[1].score)
    }

    private fun recommendationService(
        userRepository: UserRepository = FakeUserRepository(),
        vendorRepository: VendorRepository = FakeVendorRepository(),
        agencyRepository: AgencyRepository = FakeAgencyRepository(),
        contractRepository: ContractRepository = FakeContractRepository(),
    ): RecommendationService {
        return RecommendationService(
            userRepository = userRepository,
            vendorRepository = vendorRepository,
            agencyRepository = agencyRepository,
            contractRepository = contractRepository,
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
        userId: UUID,
        mainRegion: String,
    ): Vendor {
        return Vendor.create(
            id = UUID.randomUUID(),
            userId = userId,
            businessName = "테스트 화주",
            businessRegistrationNumber = "123-45-67890",
            representativeName = "김화주",
            phoneNumber = "010-1234-5678",
            postalCode = "15360",
            address = "경기도 안산시 상록구 일동",
            addressDetail = "101호",
            mainRegion = mainRegion,
        )
    }

    private fun agency(
        name: String,
        mainRegion: String,
        serviceRegions: List<String>,
    ): Agency {
        return Agency.create(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            carrier = Carrier.CJ,
            agencyName = name,
            businessRegistrationNumber = "123-45-67890",
            representativeName = "김대리",
            phoneNumber = "010-1234-5678",
            postalCode = "15360",
            address = mainRegion,
            addressDetail = "1층",
            mainRegion = mainRegion,
            serviceRegions = serviceRegions,
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

    private class FakeVendorRepository(
        vararg seedVendors: Vendor,
    ) : VendorRepository {
        private val vendors = seedVendors.associateBy(Vendor::id)

        override fun save(vendor: Vendor): Vendor = vendor
        override fun findById(id: UUID): Vendor? = vendors[id]
        override fun findAllByIds(ids: Collection<UUID>): List<Vendor> = vendors.values.filter { it.id in ids }
        override fun findByUserId(userId: UUID): Vendor? = vendors.values.firstOrNull { it.userId == userId }
        override fun existsByUserId(userId: UUID): Boolean = vendors.values.any { it.userId == userId }
    }

    private class FakeAgencyRepository(
        vararg seedAgencies: Agency,
    ) : AgencyRepository {
        private val agencies = seedAgencies.associateBy(Agency::id)

        override fun save(agency: Agency): Agency = agency
        override fun findById(id: UUID): Agency? = agencies[id]
        override fun findAllByIds(ids: Collection<UUID>): List<Agency> = agencies.values.filter { it.id in ids }
        override fun findAll(condition: AgencySearchCondition, pageable: Pageable): Page<Agency> = PageImpl(agencies.values.toList(), pageable, agencies.size.toLong())
        override fun findAllForRecommendation(): List<Agency> = agencies.values.toList()
        override fun findByUserId(userId: UUID): Agency? = agencies.values.firstOrNull { it.userId == userId }
        override fun existsByUserId(userId: UUID): Boolean = agencies.values.any { it.userId == userId }
    }

    private class FakeContractRepository(
        private vararg val previousAgencyIds: UUID,
    ) : ContractRepository {
        override fun save(contract: Contract): Contract = contract
        override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<Contract> = PageImpl(emptyList(), pageable, 0)
        override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Contract> = PageImpl(emptyList(), pageable, 0)
        override fun findRecentAgencyIdsByVendorId(vendorId: UUID, limit: Int): List<UUID> = previousAgencyIds.take(limit)
        override fun existsByContractRequestId(contractRequestId: UUID): Boolean = false
    }
}
