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

    @Test
    fun `게스트 access token은 GUEST 역할과 임시 ID를 포함한다`() {
        // Given: DB 사용자와 연결되지 않은 임시 게스트 ID를 준비한다.
        val guestId = UUID.randomUUID()

        // When: 게스트 access token을 발급하고 access token으로 해석한다.
        val token = tokenProvider.generateGuestAccessToken(guestId)
        val claims = tokenProvider.parseAccessToken(token)

        // Then: 토큰 subject는 임시 ID이고 권한은 GUEST여야 한다.
        assertEquals(guestId, claims.userId)
        assertEquals(UserRole.GUEST, claims.role)
    }

    @Test
    fun `게스트 access token은 refresh token으로 사용할 수 없다`() {
        // Given: refresh token을 발급하지 않는 게스트의 access token을 준비한다.
        val token = tokenProvider.generateGuestAccessToken(UUID.randomUUID())

        // When & Then: 토큰 종류가 ACCESS이므로 refresh token 검증은 실패해야 한다.
        assertThrows(GlobalException::class.java) {
            tokenProvider.parseRefreshToken(token)
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
