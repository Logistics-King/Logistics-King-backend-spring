package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.util.UUID

class AuthServiceTest {

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
        return AuthService(
            userRepository = FakeUserRepository(user),
            passwordManager = FakePasswordManager(),
            tokenProvider = FakeTokenProvider(user),
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
        private val user: User,
    ) : UserRepository {
        override fun findById(id: UUID): User? {
            return user.takeIf { it.id == id }
        }

        override fun findByLoginId(loginId: String): User? {
            return user.takeIf { it.loginId == loginId }
        }

        override fun save(user: User): User {
            return user
        }
    }

    private class FakePasswordManager : PasswordManager {
        override fun matches(
            rawPassword: String,
            encodedPassword: String,
        ): Boolean {
            return rawPassword == "password"
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
