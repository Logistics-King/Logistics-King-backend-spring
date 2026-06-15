package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorProductCommand
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.util.UUID

class VendorServiceTest {

    @Test
    fun `create 성공 시 화주 프로필을 저장한다`() {
        val user = user(role = UserRole.VENDOR)
        val vendorId = UUID.randomUUID()
        val service = vendorService(
            userRepository = FakeUserRepository(user),
            idGenerator = FakeIdGenerator(vendorId),
        )

        val result = service.create(createVendorCommand(user.id))

        assertEquals(vendorId, result.vendorId)
        assertEquals(user.id, result.userId)
        assertEquals("안산 옷가게", result.businessName)
    }

    @Test
    fun `create 시 화주 권한이 아니면 예외가 발생한다`() {
        val user = user(role = UserRole.AGENCY)
        val service = vendorService(userRepository = FakeUserRepository(user))

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createVendorCommand(user.id))
        }

        assertEquals(VendorErrorCode.USER_IS_NOT_VENDOR, exception.errorCode)
    }

    @Test
    fun `create 시 이미 화주 프로필이 있으면 예외가 발생한다`() {
        val user = user(role = UserRole.VENDOR)
        val vendorRepository = FakeVendorRepository()
        val service = vendorService(
            userRepository = FakeUserRepository(user),
            vendorRepository = vendorRepository,
        )
        service.create(createVendorCommand(user.id))

        val exception = assertThrows(GlobalException::class.java) {
            service.create(createVendorCommand(user.id))
        }

        assertEquals(VendorErrorCode.VENDOR_ALREADY_EXISTS, exception.errorCode)
    }

    @Test
    fun `createProduct 성공 시 화주 배송 품목을 저장한다`() {
        val user = user(role = UserRole.VENDOR)
        val vendorRepository = FakeVendorRepository()
        val productRepository = FakeVendorProductRepository()
        val service = vendorService(
            userRepository = FakeUserRepository(user),
            vendorRepository = vendorRepository,
            vendorProductRepository = productRepository,
            idGenerator = QueueIdGenerator(UUID.randomUUID(), UUID.randomUUID()),
        )
        service.create(createVendorCommand(user.id))

        val result = service.createProduct(createVendorProductCommand(user.id))

        assertEquals(ProductCategory.CLOTHING, result.category)
        assertEquals("여성 의류", result.name)
        assertEquals(BigDecimal("25000"), result.averagePrice)
        assertEquals(800, result.boxQuantity)
        assertEquals(0, result.itemQuantity)
    }

    @Test
    fun `getProducts는 물품명 카테고리 박스 사이즈로 검색한다`() {
        val user = user(role = UserRole.VENDOR)
        val vendorRepository = FakeVendorRepository()
        val productRepository = FakeVendorProductRepository()
        val service = vendorService(
            userRepository = FakeUserRepository(user),
            vendorRepository = vendorRepository,
            vendorProductRepository = productRepository,
            idGenerator = QueueIdGenerator(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
        )
        service.create(createVendorCommand(user.id))
        service.createProduct(createVendorProductCommand(user.id))
        service.createProduct(
            createVendorProductCommand(
                userId = user.id,
                category = ProductCategory.ELECTRONICS,
                name = "노트북",
                boxSize = BoxSize.SIZE_100,
            )
        )

        val result = service.getProducts(
            userId = user.id,
            condition = VendorProductSearchCondition(
                name = "의류",
                category = ProductCategory.CLOTHING,
                boxSize = BoxSize.SIZE_60,
                coldChainType = null,
            ),
            pageable = PageRequest.of(0, 20),
        )

        assertEquals(1, result.totalElements)
        assertEquals("여성 의류", result.content.first().name)
        assertEquals(BoxSize.SIZE_60, result.content.first().boxSize)
    }

    @Test
    fun `createProduct 시 화주 프로필이 없으면 예외가 발생한다`() {
        val user = user(role = UserRole.VENDOR)
        val service = vendorService(userRepository = FakeUserRepository(user))

        val exception = assertThrows(GlobalException::class.java) {
            service.createProduct(createVendorProductCommand(user.id))
        }

        assertEquals(VendorErrorCode.VENDOR_NOT_FOUND, exception.errorCode)
    }

    private fun vendorService(
        userRepository: FakeUserRepository = FakeUserRepository(user(role = UserRole.VENDOR)),
        vendorRepository: FakeVendorRepository = FakeVendorRepository(),
        vendorProductRepository: FakeVendorProductRepository = FakeVendorProductRepository(),
        idGenerator: IdGenerator = FakeIdGenerator(UUID.randomUUID()),
    ): VendorService {
        return VendorService(
            userRepository = userRepository,
            vendorRepository = vendorRepository,
            vendorProductRepository = vendorProductRepository,
            idGenerator = idGenerator,
        )
    }

    private fun createVendorCommand(userId: UUID): CreateVendorCommand {
        return CreateVendorCommand(
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

    private fun createVendorProductCommand(
        userId: UUID,
        category: ProductCategory = ProductCategory.CLOTHING,
        name: String = "여성 의류",
        boxSize: BoxSize = BoxSize.SIZE_60,
    ): CreateVendorProductCommand {
        return CreateVendorProductCommand(
            userId = userId,
            category = category,
            name = name,
            description = "일반 의류",
            averagePrice = BigDecimal("25000"),
            averageWeightGram = 700,
            boxSize = boxSize,
            boxQuantity = 800,
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

    private fun user(role: UserRole): User {
        return User.create(
            id = UUID.randomUUID(),
            loginId = "vendor01",
            email = "vendor01@example.com",
            encodedPassword = "encoded-password",
            name = "vendor",
            role = role,
        )
    }

    private class FakeUserRepository(
        vararg seedUsers: User,
    ) : UserRepository {
        constructor(user: User? = null) : this(*listOfNotNull(user).toTypedArray())

        private val users = seedUsers.associateBy { it.id }.toMutableMap()

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

    private class FakeVendorRepository : VendorRepository {
        private val vendors = mutableMapOf<UUID, Vendor>()

        override fun save(vendor: Vendor): Vendor {
            vendors[vendor.id] = vendor
            return vendor
        }

        override fun findById(id: UUID): Vendor? {
            return vendors[id]
        }

        override fun findByUserId(userId: UUID): Vendor? {
            return vendors.values.firstOrNull { it.userId == userId }
        }

        override fun existsByUserId(userId: UUID): Boolean {
            return vendors.values.any { it.userId == userId }
        }
    }

    private class FakeVendorProductRepository : VendorProductRepository {
        private val products = mutableMapOf<UUID, VendorProduct>()

        override fun save(product: VendorProduct): VendorProduct {
            products[product.id] = product
            return product
        }

        override fun findByIdAndVendorId(
            id: UUID,
            vendorId: UUID,
        ): VendorProduct? {
            return products[id]?.takeIf { it.vendorId == vendorId }
        }

        override fun findAllByVendorId(
            vendorId: UUID,
            condition: VendorProductSearchCondition,
            pageable: Pageable,
        ): Page<VendorProduct> {
            val filteredProducts = filterProducts(condition) { it.vendorId == vendorId }

            return PageImpl(filteredProducts, pageable, filteredProducts.size.toLong())
        }

        private fun filterProducts(
            condition: VendorProductSearchCondition,
            extraPredicate: (VendorProduct) -> Boolean = { true },
        ): List<VendorProduct> {
            return products.values.filter {
                extraPredicate(it) &&
                    (condition.normalizedName == null || it.name.contains(condition.normalizedName, ignoreCase = true)) &&
                    (condition.category == null || it.category == condition.category) &&
                    (condition.boxSize == null || it.boxSize == condition.boxSize) &&
                    (condition.coldChainType == null || it.coldChainType == condition.coldChainType)
            }
        }
    }

    private class FakeIdGenerator(
        private val id: UUID,
    ) : IdGenerator {
        override fun generate(): UUID {
            return id
        }
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
