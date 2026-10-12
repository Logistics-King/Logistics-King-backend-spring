package logisticsking.com.logisticskingbackendspring.app.auth

import logisticsking.com.logisticskingbackendspring.app.auth.result.GuestAccessResult
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.GuestAccessUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LoginUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LogoutUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RefreshTokenUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RequestLoginIdRecoveryUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RequestPasswordResetUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.ResetPasswordUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.SignUpUseCase
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.TokenCookieManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.mock.web.MockHttpServletResponse
import java.util.UUID

class AuthControllerTest {

    @Test
    fun `게스트 접근 발급 시 access token을 설정하고 기존 refresh token을 만료한다`() {
        // Given: 임시 게스트 ID와 게스트 access token을 반환하는 유스케이스를 준비한다.
        val guestId = UUID.randomUUID()
        val controller = controller(
            guestAccessUseCase = object : GuestAccessUseCase {
                override fun accessAsGuest(): GuestAccessResult {
                    return GuestAccessResult(
                        guestId = guestId,
                        role = UserRole.GUEST,
                        accessToken = "guest-access-token",
                    )
                }
            }
        )
        val servletResponse = MockHttpServletResponse()

        // When: 요청 본문 없이 게스트 접근 API를 호출한다.
        val response = controller.accessAsGuest(servletResponse)

        // Then: 응답에는 임시 ID와 GUEST 역할이 포함되어야 한다.
        assertEquals(guestId.toString(), response.payload.response?.guestId)
        assertEquals(UserRole.GUEST.name, response.payload.response?.role)

        // Then: 브라우저는 게스트 access token을 받고 이전 일반 회원 refresh token은 제거해야 한다.
        val setCookies = servletResponse.getHeaders("Set-Cookie")
        assertEquals(2, setCookies.size)
        assertTrue(setCookies.any { cookie -> cookie.startsWith("accessToken=guest-access-token") })
        assertTrue(setCookies.any { cookie -> cookie.startsWith("refreshToken=") && cookie.contains("Max-Age=0") })
    }

    private fun controller(guestAccessUseCase: GuestAccessUseCase): AuthController {
        return AuthController(
            signUpUseCase = mock(SignUpUseCase::class.java),
            loginUseCase = mock(LoginUseCase::class.java),
            guestAccessUseCase = guestAccessUseCase,
            refreshTokenUseCase = mock(RefreshTokenUseCase::class.java),
            logoutUseCase = mock(LogoutUseCase::class.java),
            requestLoginIdRecoveryUseCase = mock(RequestLoginIdRecoveryUseCase::class.java),
            requestPasswordResetUseCase = mock(RequestPasswordResetUseCase::class.java),
            resetPasswordUseCase = mock(ResetPasswordUseCase::class.java),
            tokenCookieManager = TokenCookieManager(
                accessTokenName = "accessToken",
                refreshTokenName = "refreshToken",
                path = "/",
                httpOnly = true,
                secure = false,
                sameSite = "Lax",
                accessTokenExpirationSeconds = 900,
                refreshTokenExpirationSeconds = 1209600,
            ),
        )
    }
}
