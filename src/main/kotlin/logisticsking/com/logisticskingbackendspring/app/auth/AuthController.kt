package logisticsking.com.logisticskingbackendspring.app.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.app.auth.dto.AuthRequest
import logisticsking.com.logisticskingbackendspring.app.auth.dto.AuthResponse
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LoginUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LogoutUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RefreshTokenUseCase
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.domain.auth.AuthErrorCode
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.infra.security.TokenCookieManager
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenCookieManager: TokenCookieManager,
) {

    @PostMapping("/login")
    fun login(
        @RequestBody request: AuthRequest.Login,
        response: HttpServletResponse,
    ): ApiResponse<AuthResponse.Login> {
        val result = loginUseCase.login(request.toCommand())
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookieManager.createAccessTokenCookie(result.accessToken).toString())
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookieManager.createRefreshTokenCookie(result.refreshToken).toString())

        return ApiResponse.success(
            response = AuthResponse.Login(
                userId = result.userId.toString(),
                role = result.role.name,
            )
        )
    }

    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<AuthResponse.Refresh> {
        val refreshToken = tokenCookieManager.extractRefreshToken(request)
            ?: throw GlobalException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)
        val result = refreshTokenUseCase.refresh(RefreshTokenCommand(refreshToken))
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookieManager.createAccessTokenCookie(result.accessToken).toString())
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookieManager.createRefreshTokenCookie(result.refreshToken).toString())

        return ApiResponse.success(
            response = AuthResponse.Refresh(
                userId = result.userId.toString(),
                role = result.role.name,
            )
        )
    }

    @PostMapping("/logout")
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): ApiResponse<AuthResponse.Logout> {
        val result = logoutUseCase.logout(
            LogoutCommand(
                refreshToken = tokenCookieManager.extractRefreshToken(request),
            )
        )
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookieManager.expireAccessTokenCookie().toString())
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookieManager.expireRefreshTokenCookie().toString())

        return ApiResponse.success(
            response = AuthResponse.Logout(
                loggedOut = result.loggedOut,
            )
        )
    }
}
