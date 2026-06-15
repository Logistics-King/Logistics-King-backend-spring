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
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RequestLoginIdRecoveryUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RequestPasswordResetUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.ResetPasswordUseCase
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
    private val requestLoginIdRecoveryUseCase: RequestLoginIdRecoveryUseCase,
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
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

    @Operation(
        summary = "아이디 찾기 이메일 발송 요청",
        description = "이름과 이메일이 일치하면 로그인 ID를 이메일로 발송합니다. 계정 존재 여부는 응답으로 노출하지 않습니다.",
    )
    @PostMapping("/recovery/login-id")
    fun requestLoginIdRecovery(
        @Valid @RequestBody request: AuthRequest.RequestLoginIdRecovery,
    ): ApiResponse<AuthResponse.AccountRecoveryRequest> {
        val result = requestLoginIdRecoveryUseCase.requestLoginIdRecovery(request.toCommand())

        return ApiResponse.success(
            response = AuthResponse.AccountRecoveryRequest(
                accepted = result.accepted,
                message = ACCOUNT_RECOVERY_ACCEPTED_MESSAGE,
            )
        )
    }

    @Operation(
        summary = "비밀번호 재설정 이메일 인증 요청",
        description = "로그인 ID와 이메일이 일치하면 1분 동안 유효한 비밀번호 재설정 토큰을 이메일로 발송합니다. 계정 존재 여부는 응답으로 노출하지 않습니다.",
    )
    @PostMapping("/password-reset/request")
    fun requestPasswordReset(
        @Valid @RequestBody request: AuthRequest.RequestPasswordReset,
    ): ApiResponse<AuthResponse.AccountRecoveryRequest> {
        val result = requestPasswordResetUseCase.requestPasswordReset(request.toCommand())

        return ApiResponse.success(
            response = AuthResponse.AccountRecoveryRequest(
                accepted = result.accepted,
                message = ACCOUNT_RECOVERY_ACCEPTED_MESSAGE,
            )
        )
    }

    @Operation(
        summary = "비밀번호 재설정 확정",
        description = "이메일로 받은 재설정 토큰을 검증하고 새 비밀번호로 변경합니다.",
    )
    @PostMapping("/password-reset/confirm")
    fun resetPassword(
        @Valid @RequestBody request: AuthRequest.ResetPassword,
    ): ApiResponse<AuthResponse.ResetPassword> {
        val result = resetPasswordUseCase.resetPassword(request.toCommand())

        return ApiResponse.success(
            response = AuthResponse.ResetPassword(
                reset = result.reset,
            )
        )
    }

    private companion object {
        private const val ACCOUNT_RECOVERY_ACCEPTED_MESSAGE = "입력한 정보가 일치하면 이메일을 발송합니다."
    }
}
