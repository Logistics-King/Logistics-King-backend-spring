package logisticsking.com.logisticskingbackendspring.infra.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import logisticsking.com.logisticskingbackendspring.domain.auth.TokenProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class JwtAuthenticationFilterTest {

    @Test
    fun `유효하지 않은 access token이면 401을 응답한다`() {
        val filter = JwtAuthenticationFilter(
            tokenProvider = tokenProvider(),
            tokenCookieManager = tokenCookieManager(),
            securityResponseWriter = SecurityResponseWriter(ObjectMapper()),
        )
        val request = MockHttpServletRequest("GET", "/api/v1/users").apply {
            servletPath = "/api/v1/users"
            setCookies(Cookie("accessToken", "invalid-token"))
        }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, MockFilterChain())

        assertEquals(401, response.status)
    }

    private fun tokenProvider(): TokenProvider {
        return JwtTokenProvider(
            secret = "test-secret-key-for-logistics-king-auth",
            accessTokenExpirationSeconds = 900,
            refreshTokenExpirationSeconds = 1209600,
        )
    }

    private fun tokenCookieManager(): TokenCookieManager {
        return TokenCookieManager(
            accessTokenName = "accessToken",
            refreshTokenName = "refreshToken",
            path = "/",
            httpOnly = true,
            secure = false,
            sameSite = "Lax",
            accessTokenExpirationSeconds = 900,
            refreshTokenExpirationSeconds = 1209600,
        )
    }
}
