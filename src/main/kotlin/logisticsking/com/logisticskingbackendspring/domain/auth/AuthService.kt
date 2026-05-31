package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpCommand
import logisticsking.com.logisticskingbackendspring.app.auth.result.LoginResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.LogoutResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.RefreshTokenResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.SignUpResult
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LoginUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LogoutUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RefreshTokenUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.SignUpUseCase
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserErrorCode
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val idGenerator: IdGenerator,
    private val passwordManager: PasswordManager,
    private val tokenProvider: TokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${auth.jwt.refresh-token-expiration-seconds}") private val refreshTokenExpirationSeconds: Long,
) : SignUpUseCase,
    LoginUseCase,
    RefreshTokenUseCase,
    LogoutUseCase {

    override fun signUp(command: SignUpCommand): SignUpResult {
        val loginId = command.loginId.trim()
        val email = command.email.trim()
        val name = command.name.trim()

        requireDomain(command.password.isNotBlank(), UserErrorCode.INVALID_PASSWORD)

        if (userRepository.existsByLoginId(loginId)) {
            throw GlobalException(UserErrorCode.DUPLICATED_LOGIN_ID)
        }
        if (userRepository.existsByEmail(email)) {
            throw GlobalException(UserErrorCode.DUPLICATED_EMAIL)
        }

        val user = User.create(
            id = idGenerator.generate(),
            loginId = loginId,
            email = email,
            encodedPassword = passwordManager.encode(command.password),
            name = name,
            role = command.role,
        )
        val saved = userRepository.save(user)

        return SignUpResult(
            userId = saved.id,
            role = saved.role,
        )
    }

    override fun login(command: LoginCommand): LoginResult {
        val user = userRepository.findByLoginId(command.loginId)
            ?: throw GlobalException(AuthErrorCode.INVALID_CREDENTIALS)

        if (!passwordManager.matches(command.password, user.encodedPassword)) {
            throw GlobalException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        val accessToken = tokenProvider.generateAccessToken(user)
        val refreshToken = tokenProvider.generateRefreshToken(user)
        refreshTokenRepository.save(
            userId = user.id,
            token = refreshToken,
            ttl = Duration.ofSeconds(refreshTokenExpirationSeconds),
        )

        return LoginResult(
            userId = user.id,
            role = user.role,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    override fun refresh(command: RefreshTokenCommand): RefreshTokenResult {
        val claims = tokenProvider.parseRefreshToken(command.refreshToken)
        val storedToken = refreshTokenRepository.findByUserId(claims.userId)
            ?: throw GlobalException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)

        if (storedToken != command.refreshToken) {
            throw GlobalException(AuthErrorCode.INVALID_TOKEN)
        }

        val user = userRepository.findById(claims.userId)
            ?: throw GlobalException(AuthErrorCode.INVALID_TOKEN)

        val accessToken = tokenProvider.generateAccessToken(user)
        val refreshToken = tokenProvider.generateRefreshToken(user)
        refreshTokenRepository.save(
            userId = user.id,
            token = refreshToken,
            ttl = Duration.ofSeconds(refreshTokenExpirationSeconds),
        )

        return RefreshTokenResult(
            userId = user.id,
            role = user.role,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    override fun logout(command: LogoutCommand): LogoutResult {
        if (command.refreshToken != null) {
            val claims = tokenProvider.parseRefreshToken(command.refreshToken)
            refreshTokenRepository.deleteByUserId(claims.userId)
        }

        return LogoutResult(loggedOut = true)
    }
}
