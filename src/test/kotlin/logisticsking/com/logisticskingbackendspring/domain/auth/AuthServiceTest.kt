package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpCommand
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
            SignUpCommand(
                loginId = "agency01",
                email = "agency01@example.com",
                password = "password",
                passwordConfirm = "password",
                name = "CJ 일동대리점",
                role = UserRole.AGENCY,
            )
        )

        val saved = userRepository.findByLoginId("agency01")
        assertEquals(userId, result.userId)
        assertEquals(UserRole.AGENCY, result.role)
        assertEquals("encoded-password", saved?.encodedPassword)
    }

    @Test
    fun `signUp 시 로그인 ID가 중복되면 예외가 발생한다`() {
        val user = user()
        val service = authService(userRepository = FakeUserRepository(user))

        assertThrows(GlobalException::class.java) {
            service.signUp(
                SignUpCommand(
                    loginId = user.loginId,
                    email = "new@example.com",
                    password = "password",
                    passwordConfirm = "password",
                    name = "new user",
                    role = UserRole.VENDOR,
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
                SignUpCommand(
                    loginId = "new-login-id",
                    email = user.email,
                    password = "password",
                    passwordConfirm = "password",
                    name = "new user",
                    role = UserRole.VENDOR,
                )
            )
        }
    }

    @Test
    fun `signUp 시 비밀번호 확인이 일치하지 않으면 예외가 발생한다`() {
        val service = authService()

        val exception = assertThrows(GlobalException::class.java) {
            service.signUp(
                SignUpCommand(
                    loginId = "vendor01",
                    email = "vendor01@example.com",
                    password = "password",
                    passwordConfirm = "different-password",
                    name = "new user",
                    role = UserRole.VENDOR,
                )
            )
        }

        assertEquals(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH, exception.errorCode)
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
        refreshTokenRepository: FakeRefreshTokenRepository = FakeRefreshTokenRepository(),
    ): AuthService {
        return AuthService(
            userRepository = userRepository,
            idGenerator = idGenerator,
            passwordManager = FakePasswordManager(),
            tokenProvider = FakeTokenProvider(tokenUser),
            refreshTokenRepository = refreshTokenRepository,
            refreshTokenExpirationSeconds = 1209600,
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
        private val users = user
            ?.let { mutableMapOf(it.id to it) }
            ?: mutableMapOf()

        override fun findById(id: UUID): User? {
            return users[id]
        }

        override fun findByLoginId(loginId: String): User? {
            return users.values.firstOrNull { it.loginId == loginId }
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

        override fun save(
            userId: UUID,
            token: String,
            ttl: Duration,
        ) {
            tokens[userId] = token
        }

        override fun findByUserId(userId: UUID): String? {
            return tokens[userId]
        }

        override fun deleteByUserId(userId: UUID) {
            tokens.remove(userId)
        }
    }
}
