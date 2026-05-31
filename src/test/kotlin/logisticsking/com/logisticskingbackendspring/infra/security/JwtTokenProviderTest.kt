package logisticsking.com.logisticskingbackendspring.infra.security

import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class JwtTokenProviderTest {

    private val tokenProvider = JwtTokenProvider(
        secret = "test-secret-key-for-logistics-king-auth",
        accessTokenExpirationSeconds = 900,
        refreshTokenExpirationSeconds = 1209600,
    )

    @Test
    fun `access token을 발급하고 검증한다`() {
        val user = user()

        val token = tokenProvider.generateAccessToken(user)
        val claims = tokenProvider.parseAccessToken(token)

        assertEquals(user.id, claims.userId)
        assertEquals(user.role, claims.role)
    }

    @Test
    fun `refresh token은 access token으로 검증할 수 없다`() {
        val user = user()

        val token = tokenProvider.generateRefreshToken(user)

        assertThrows(GlobalException::class.java) {
            tokenProvider.parseAccessToken(token)
        }
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
}
