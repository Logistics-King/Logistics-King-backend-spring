package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.app.agency.command.CreateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.CreateAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RequestLoginIdRecoveryCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RequestPasswordResetCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.ResetPasswordCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpVendorProfileCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.command.CreateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.result.DeliverResult
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.CreateDeliverUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.CreateVendorUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

class AuthServiceTest {

    @Test
    fun `signUp 성공 시 비밀번호를 암호화하고 유저를 저장한다`() {
        val userId = UUID.randomUUID()
        val userRepository = FakeUserRepository()
        val service = authService(
            userRepository = userRepository,
            idGenerator = FakeIdGenerator(userId),
        )

        val result = service.signUp(
            signUpVendorCommand(
                loginId = "vendor01",
                email = "vendor01@example.com",
                password = "password",
                passwordConfirm = "password",
                name = "안산 옷가게",
            )
        )

        val saved = userRepository.findByLoginId("vendor01")
        assertEquals(userId, result.userId)
        assertEquals(UserRole.VENDOR, result.role)
        assertEquals("encoded-password", saved?.encodedPassword)
    }

    @Test
    fun `signUp 시 로그인 ID가 중복되면 예외가 발생한다`() {
        val user = user()
        val service = authService(userRepository = FakeUserRepository(user))

        assertThrows(GlobalException::class.java) {
            service.signUp(
                signUpVendorCommand(
                    loginId = user.loginId,
                    email = "new@example.com",
                    password = "password",
                    passwordConfirm = "password",
                    name = "new user",
                )
            )
        }
    }

    @Test
    fun `signUp 시 이메일이 중복되면 예외가 발생한다`() {
        val user = user()
        val service = authService(userRepository = FakeUserRepository(user))

        assertThrows(GlobalException::class.java) {
            service.signUp(
                signUpVendorCommand(
                    loginId = "new-login-id",
                    email = user.email,
                    password = "password",
                    passwordConfirm = "password",
                    name = "new user",
                )
            )
        }
    }

    @Test
    fun `signUp 시 비밀번호 확인이 일치하지 않으면 예외가 발생한다`() {
        val service = authService()

        val exception = assertThrows(GlobalException::class.java) {
            service.signUp(
                signUpVendorCommand(
                    loginId = "vendor01",
                    email = "vendor01@example.com",
                    password = "password",
                    passwordConfirm = "different-password",
                    name = "new user",
                )
            )
        }

        assertEquals(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH, exception.errorCode)
    }

    @Test
    fun `일반 회원가입 흐름으로 GUEST 계정을 생성할 수 없다`() {
        // Given: 일반 회원가입 요청이 임시 접근 전용 역할인 GUEST를 지정한다.
        val service = authService()
        val command = SignUpCommand(
            loginId = "guest",
            email = "guest@example.com",
            password = "password",
            passwordConfirm = "password",
            name = "guest",
            role = UserRole.GUEST,
        )

        // When: 비밀번호와 프로필을 사용하는 일반 회원가입 흐름으로 게스트 생성을 시도한다.
        val exception = assertThrows(GlobalException::class.java) {
            service.signUp(command)
        }

        // Then: 게스트는 별도 임시 접근 API로만 발급해야 하므로 일반 회원가입을 거부한다.
        assertEquals(AuthErrorCode.INVALID_SIGN_UP_PROFILE, exception.errorCode)
    }

    @Test
    fun `게스트 접근 발급 시 계정을 저장하지 않고 GUEST access token만 반환한다`() {
        // Given: DB에 저장되지 않을 고정 guestId와 비어 있는 사용자·refresh token 저장소를 준비한다.
        val guestId = UUID.randomUUID()
        val userRepository = FakeUserRepository()
        val refreshTokenRepository = FakeRefreshTokenRepository()
        val tokenProvider = FakeTokenProvider(user())
        val service = authService(
            userRepository = userRepository,
            idGenerator = FakeIdGenerator(guestId),
            tokenProvider = tokenProvider,
            refreshTokenRepository = refreshTokenRepository,
        )

        // When: 비밀번호나 회원 프로필 없이 게스트 접근 권한을 발급한다.
        val result = service.accessAsGuest()

        // Then: 임시 ID와 GUEST access token을 반환하고 영속 계정과 refresh token은 만들지 않는다.
        assertEquals(guestId, result.guestId)
        assertEquals(UserRole.GUEST, result.role)
        assertEquals("guest-access-$guestId", result.accessToken)
        assertEquals(0, userRepository.saveCount)
        assertEquals(0, refreshTokenRepository.saveCount)
    }

    @Test
    fun `login 성공 시 refresh token을 저장한다`() {
        val user = user()
        val refreshTokenRepository = FakeRefreshTokenRepository()
        val service = authService(
            user = user,
            refreshTokenRepository = refreshTokenRepository,
        )

        val result = service.login(
            LoginCommand(
                loginId = user.loginId,
                password = "password",
            )
        )

        assertEquals(user.id, result.userId)
        assertEquals(result.refreshToken, refreshTokenRepository.findByUserId(user.id))
    }

    @Test
    fun `refresh 성공 시 refresh token을 회전 저장한다`() {
        val user = user()
        val refreshTokenRepository = FakeRefreshTokenRepository()
        val service = authService(
            user = user,
            refreshTokenRepository = refreshTokenRepository,
        )
        val loginResult = service.login(LoginCommand(user.loginId, "password"))

        val refreshResult = service.refresh(RefreshTokenCommand(loginResult.refreshToken))

        assertEquals(user.id, refreshResult.userId)
        assertEquals(refreshResult.refreshToken, refreshTokenRepository.findByUserId(user.id))
    }

    @Test
    fun `logout 시 refresh token을 삭제한다`() {
        val user = user()
        val refreshTokenRepository = FakeRefreshTokenRepository()
        val service = authService(
            user = user,
            refreshTokenRepository = refreshTokenRepository,
        )
        val loginResult = service.login(LoginCommand(user.loginId, "password"))

        service.logout(LogoutCommand(loginResult.refreshToken))

        assertNull(refreshTokenRepository.findByUserId(user.id))
    }

    @Test
    fun `아이디 찾기 요청 시 이름과 이메일이 일치하면 로그인 ID 이메일을 발송한다`() {
        val user = user()
        val emailSender = FakeAccountRecoveryEmailSender()
        val service = authService(
            userRepository = FakeUserRepository(user),
            accountRecoveryEmailSender = emailSender,
        )

        val result = service.requestLoginIdRecovery(
            RequestLoginIdRecoveryCommand(
                name = user.name,
                email = user.email,
            )
        )

        assertEquals(true, result.accepted)
        assertEquals(user.loginId, emailSender.sentLoginId)
    }

    @Test
    fun `비밀번호 재설정 요청 시 1분 만료 토큰을 저장하고 이메일을 발송한다`() {
        val user = user()
        val tokenRepository = FakeAccountRecoveryTokenRepository()
        val emailSender = FakeAccountRecoveryEmailSender()
        val service = authService(
            userRepository = FakeUserRepository(user),
            accountRecoveryTokenRepository = tokenRepository,
            accountRecoveryEmailSender = emailSender,
        )

        val result = service.requestPasswordReset(
            RequestPasswordResetCommand(
                loginId = user.loginId,
                email = user.email,
            )
        )

        val savedToken = tokenRepository.findByTokenHashAndPurpose("hash-reset-token", AccountRecoveryTokenPurpose.RESET_PASSWORD)
        assertEquals(true, result.accepted)
        assertEquals("reset-token", emailSender.sentPasswordResetToken)
        checkNotNull(savedToken)
        val ttlSeconds = Duration.between(LocalDateTime.now(), savedToken.expiresAt).seconds
        assert(ttlSeconds in 58..60)
    }

    @Test
    fun `비밀번호 재설정 확정 시 비밀번호를 변경하고 refresh token을 삭제한다`() {
        val user = user()
        val userRepository = FakeUserRepository(user)
        val tokenRepository = FakeAccountRecoveryTokenRepository()
        val refreshTokenRepository = FakeRefreshTokenRepository()
        val service = authService(
            userRepository = userRepository,
            refreshTokenRepository = refreshTokenRepository,
            accountRecoveryTokenRepository = tokenRepository,
        )
        refreshTokenRepository.save(user.id, "refresh-token", Duration.ofMinutes(10))
        tokenRepository.save(
            AccountRecoveryToken.create(
                id = UUID.randomUUID(),
                userId = user.id,
                purpose = AccountRecoveryTokenPurpose.RESET_PASSWORD,
                tokenHash = "hash-reset-token",
                expiresAt = LocalDateTime.now().plusMinutes(1),
            )
        )

        val result = service.resetPassword(
            ResetPasswordCommand(
                token = "reset-token",
                newPassword = "new-password",
                newPasswordConfirm = "new-password",
            )
        )

        assertEquals(true, result.reset)
        assertEquals("encoded-new-password", userRepository.findById(user.id)?.encodedPassword)
        assertNull(refreshTokenRepository.findByUserId(user.id))
        val usedToken = tokenRepository.findByTokenHashAndPurpose("hash-reset-token", AccountRecoveryTokenPurpose.RESET_PASSWORD)
        checkNotNull(usedToken?.usedAt)
    }

    @Test
    fun `비밀번호 재설정 토큰이 만료되면 예외가 발생한다`() {
        val user = user()
        val tokenRepository = FakeAccountRecoveryTokenRepository()
        val service = authService(
            userRepository = FakeUserRepository(user),
            accountRecoveryTokenRepository = tokenRepository,
        )
        tokenRepository.save(
            AccountRecoveryToken.restore(
                id = UUID.randomUUID(),
                userId = user.id,
                purpose = AccountRecoveryTokenPurpose.RESET_PASSWORD,
                tokenHash = "hash-reset-token",
                expiresAt = LocalDateTime.now().minusSeconds(1),
                usedAt = null,
            )
        )

        val exception = assertThrows(GlobalException::class.java) {
            service.resetPassword(
                ResetPasswordCommand(
                    token = "reset-token",
                    newPassword = "new-password",
                    newPasswordConfirm = "new-password",
                )
            )
        }

        assertEquals(AuthErrorCode.RESET_TOKEN_EXPIRED, exception.errorCode)
    }

    private fun authService(
        user: User,
        refreshTokenRepository: FakeRefreshTokenRepository,
    ): AuthService {
        return authService(
            userRepository = FakeUserRepository(user),
            tokenUser = user,
            refreshTokenRepository = refreshTokenRepository,
        )
    }

    private fun authService(
        userRepository: FakeUserRepository = FakeUserRepository(),
        idGenerator: IdGenerator = FakeIdGenerator(UUID.randomUUID()),
        tokenUser: User = user(),
        tokenProvider: TokenProvider = FakeTokenProvider(tokenUser),
        refreshTokenRepository: FakeRefreshTokenRepository = FakeRefreshTokenRepository(),
        accountRecoveryTokenRepository: FakeAccountRecoveryTokenRepository = FakeAccountRecoveryTokenRepository(),
        accountRecoveryEmailSender: FakeAccountRecoveryEmailSender = FakeAccountRecoveryEmailSender(),
        createVendorUseCase: CreateVendorUseCase = FakeCreateVendorUseCase(),
        createAgencyUseCase: CreateAgencyUseCase = FakeCreateAgencyUseCase(),
        createDeliverUseCase: CreateDeliverUseCase = FakeCreateDeliverUseCase(),
    ): AuthService {
        return AuthService(
            userRepository = userRepository,
            idGenerator = idGenerator,
            passwordManager = FakePasswordManager(),
            tokenProvider = tokenProvider,
            refreshTokenRepository = refreshTokenRepository,
            accountRecoveryTokenRepository = accountRecoveryTokenRepository,
            accountRecoveryTokenGenerator = FakeAccountRecoveryTokenGenerator(),
            accountRecoveryEmailSender = accountRecoveryEmailSender,
            createVendorUseCase = createVendorUseCase,
            createAgencyUseCase = createAgencyUseCase,
            createDeliverUseCase = createDeliverUseCase,
            refreshTokenExpirationSeconds = 1209600,
        )
    }

    private fun signUpVendorCommand(
        loginId: String,
        email: String,
        password: String,
        passwordConfirm: String,
        name: String,
    ): SignUpCommand {
        return SignUpCommand(
            loginId = loginId,
            email = email,
            password = password,
            passwordConfirm = passwordConfirm,
            name = name,
            role = UserRole.VENDOR,
            vendorProfile = SignUpVendorProfileCommand(
                businessName = "안산 옷가게",
                businessRegistrationNumber = "123-45-67890",
                representativeName = "김사장",
                phoneNumber = "010-1234-5678",
                postalCode = "15360",
                address = "경기도 안산시 상록구 일동",
                addressDetail = "101호",
                mainRegion = "경기도 안산시 일동",
            ),
        )
    }

    private fun user(): User {
        return User.create(
            id = UUID.randomUUID(),
            loginId = "vendor01",
            email = "vendor01@example.com",
            encodedPassword = "encoded-password",
            name = "vendor",
            role = UserRole.VENDOR,
        )
    }

    private class FakeUserRepository(
        user: User? = null,
    ) : UserRepository {
        var saveCount: Int = 0
            private set

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
            saveCount += 1
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

    private class FakeCreateVendorUseCase : CreateVendorUseCase {
        override fun create(command: CreateVendorCommand): VendorResult {
            return VendorResult(
                vendorId = UUID.randomUUID(),
                userId = command.userId,
                businessName = command.businessName,
                businessRegistrationNumber = command.businessRegistrationNumber,
                representativeName = command.representativeName,
                phoneNumber = command.phoneNumber,
                postalCode = command.postalCode,
                address = command.address,
                addressDetail = command.addressDetail,
                mainRegion = command.mainRegion,
            )
        }
    }

    private class FakeCreateAgencyUseCase : CreateAgencyUseCase {
        override fun create(command: CreateAgencyCommand): AgencyResult {
            return AgencyResult(
                agencyId = UUID.randomUUID(),
                userId = command.userId,
                carrier = command.carrier,
                agencyName = command.agencyName,
                businessRegistrationNumber = command.businessRegistrationNumber,
                representativeName = command.representativeName,
                phoneNumber = command.phoneNumber,
                postalCode = command.postalCode,
                address = command.address,
                addressDetail = command.addressDetail,
                mainRegion = command.mainRegion,
                serviceRegions = command.serviceRegions,
                weekdayPickupStartTime = command.weekdayPickupStartTime,
                weekdayPickupEndTime = command.weekdayPickupEndTime,
                saturdayPickupAvailable = command.saturdayPickupAvailable,
                saturdayDeliveryAvailable = command.saturdayDeliveryAvailable,
                returnAvailable = command.returnAvailable,
                supportedColdChainTypes = command.supportedColdChainTypes,
                maxMonthlyVolume = command.maxMonthlyVolume,
            )
        }
    }

    private class FakeCreateDeliverUseCase : CreateDeliverUseCase {
        override fun create(command: CreateDeliverCommand): DeliverResult {
            return DeliverResult(
                deliverId = UUID.randomUUID(),
                userId = command.userId,
                employmentType = command.employmentType,
                agencyId = command.agencyId,
                driverName = command.driverName,
                phoneNumber = command.phoneNumber,
                vehicleNumber = command.vehicleNumber,
                serviceRegions = command.serviceRegions,
                active = command.active,
                memo = command.memo,
                agency = null,
            )
        }
    }

    private class FakePasswordManager : PasswordManager {
        override fun encode(rawPassword: String): String {
            return "encoded-$rawPassword"
        }

        override fun matches(
            rawPassword: String,
            encodedPassword: String,
        ): Boolean {
            return rawPassword == "password"
        }
    }

    private class FakeIdGenerator(
        private val id: UUID,
    ) : IdGenerator {
        override fun generate(): UUID {
            return id
        }
    }

    private class FakeTokenProvider(
        private val user: User,
    ) : TokenProvider {
        private var refreshSequence = 0

        override fun generateAccessToken(user: User): String {
            return "access-${user.id}"
        }

        override fun generateGuestAccessToken(guestId: UUID): String {
            return "guest-access-$guestId"
        }

        override fun generateRefreshToken(user: User): String {
            refreshSequence += 1
            return "refresh-${user.id}-$refreshSequence"
        }

        override fun parseAccessToken(token: String): TokenClaims {
            return claims()
        }

        override fun parseRefreshToken(token: String): TokenClaims {
            return claims()
        }

        private fun claims(): TokenClaims {
            return TokenClaims(
                userId = user.id,
                role = user.role,
                expiresAt = Instant.now().plusSeconds(1209600),
            )
        }
    }

    private class FakeRefreshTokenRepository : RefreshTokenRepository {
        private val tokens = mutableMapOf<UUID, String>()
        var saveCount: Int = 0
            private set

        override fun save(
            userId: UUID,
            token: String,
            ttl: Duration,
        ) {
            saveCount += 1
            tokens[userId] = token
        }

        override fun findByUserId(userId: UUID): String? {
            return tokens[userId]
        }

        override fun deleteByUserId(userId: UUID) {
            tokens.remove(userId)
        }
    }

    private class FakeAccountRecoveryTokenRepository : AccountRecoveryTokenRepository {
        private val tokens = mutableMapOf<UUID, AccountRecoveryToken>()

        override fun save(token: AccountRecoveryToken): AccountRecoveryToken {
            tokens[token.id] = token
            return token
        }

        override fun findByTokenHashAndPurpose(
            tokenHash: String,
            purpose: AccountRecoveryTokenPurpose,
        ): AccountRecoveryToken? {
            return tokens.values.firstOrNull { it.tokenHash == tokenHash && it.purpose == purpose }
        }

        override fun markUnusedByUserIdAndPurposeAsUsed(
            userId: UUID,
            purpose: AccountRecoveryTokenPurpose,
            usedAt: LocalDateTime,
        ) {
            tokens.values
                .filter { it.userId == userId && it.purpose == purpose && it.usedAt == null }
                .forEach { tokens[it.id] = it.use(usedAt) }
        }
    }

    private class FakeAccountRecoveryTokenGenerator : AccountRecoveryTokenGenerator {
        override fun generate(): String {
            return "reset-token"
        }

        override fun hash(rawToken: String): String {
            return "hash-$rawToken"
        }
    }

    private class FakeAccountRecoveryEmailSender : AccountRecoveryEmailSender {
        var sentLoginId: String? = null
            private set
        var sentPasswordResetToken: String? = null
            private set

        override fun sendLoginIdRecoveryEmail(
            email: String,
            loginId: String,
        ) {
            sentLoginId = loginId
        }

        override fun sendPasswordResetEmail(
            email: String,
            token: String,
            expiresAt: LocalDateTime,
        ) {
            sentPasswordResetToken = token
        }
    }
}
