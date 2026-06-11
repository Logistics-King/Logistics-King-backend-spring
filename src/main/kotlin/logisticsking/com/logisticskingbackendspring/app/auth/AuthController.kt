package logisticsking.com.logisticskingbackendspring.app.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.app.auth.dto.AuthRequest
import logisticsking.com.logisticskingbackendspring.app.auth.dto.AuthResponse
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LoginUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LogoutUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RefreshTokenUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.SignUpUseCase
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.domain.auth.AuthErrorCode
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.infra.security.TokenCookieManager
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val signUpUseCase: SignUpUseCase,
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenCookieManager: TokenCookieManager,
) {

    @Operation(summary = "화주 회원가입", description = "화주 계정을 생성합니다.")
    @PostMapping("/sign-up/vendor")
    fun signUpVendor(
        @Valid @RequestBody request: AuthRequest.SignUpVendor,
    ): ApiResponse<AuthResponse.SignUp> {
        val result = signUpUseCase.signUp(request.toCommand())

        return ApiResponse.success(
            response = AuthResponse.SignUp(
                userId = result.userId.toString(),
                role = result.role.name,
            )
        )
    }

    @Operation(summary = "대리점 회원가입", description = "택배 대리점 계정을 생성합니다.")
    @PostMapping("/sign-up/agency")
    fun signUpAgency(
        @Valid @RequestBody request: AuthRequest.SignUpAgency,
    ): ApiResponse<AuthResponse.SignUp> {
        val result = signUpUseCase.signUp(request.toCommand())

        return ApiResponse.success(
            response = AuthResponse.SignUp(
                userId = result.userId.toString(),
                role = result.role.name,
            )
        )
    }

    @Operation(summary = "배송기사 회원가입", description = "배송기사 계정을 생성합니다.")
    @PostMapping("/sign-up/driver")
    fun signUpDriver(
        @Valid @RequestBody request: AuthRequest.SignUpDriver,
    ): ApiResponse<AuthResponse.SignUp> {
        val result = signUpUseCase.signUp(request.toCommand())

        return ApiResponse.success(
            response = AuthResponse.SignUp(
                userId = result.userId.toString(),
                role = result.role.name,
            )
        )
    }

    @Operation(summary = "로그인", description = "로그인 후 access token과 refresh token을 HttpOnly cookie로 내려줍니다.")
    @PostMapping(value = ["/sign-in", "/login"])
    fun login(
        @Valid @RequestBody request: AuthRequest.Login,
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

    @Operation(
        summary = "토큰 재발급",
        description = "refresh token cookie를 검증하고 access token과 refresh token을 재발급합니다.",
        security = [SecurityRequirement(name = "refreshTokenCookie")],
    )
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

    @Operation(
        summary = "로그아웃",
        description = "Redis refresh token을 삭제하고 access/refresh cookie를 만료합니다.",
        security = [SecurityRequirement(name = "refreshTokenCookie")],
    )
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
