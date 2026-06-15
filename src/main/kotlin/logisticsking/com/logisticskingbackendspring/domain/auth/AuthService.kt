package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.LogoutCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RefreshTokenCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RequestLoginIdRecoveryCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RequestPasswordResetCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.ResetPasswordCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpCommand
import logisticsking.com.logisticskingbackendspring.app.auth.result.AccountRecoveryRequestResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.LoginResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.LogoutResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.RefreshTokenResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.ResetPasswordResult
import logisticsking.com.logisticskingbackendspring.app.auth.result.SignUpResult
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LoginUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.LogoutUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RefreshTokenUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RequestLoginIdRecoveryUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.RequestPasswordResetUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.ResetPasswordUseCase
import logisticsking.com.logisticskingbackendspring.app.auth.usecase.SignUpUseCase
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserErrorCode
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val idGenerator: IdGenerator,
    private val passwordManager: PasswordManager,
    private val tokenProvider: TokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val accountRecoveryTokenRepository: AccountRecoveryTokenRepository,
    private val accountRecoveryTokenGenerator: AccountRecoveryTokenGenerator,
    private val accountRecoveryEmailSender: AccountRecoveryEmailSender,
    @Value("\${auth.jwt.refresh-token-expiration-seconds}") private val refreshTokenExpirationSeconds: Long,
) : SignUpUseCase,
    LoginUseCase,
    RefreshTokenUseCase,
    LogoutUseCase,
    RequestLoginIdRecoveryUseCase,
    RequestPasswordResetUseCase,
    ResetPasswordUseCase {

    override fun signUp(command: SignUpCommand): SignUpResult {
        val loginId = command.loginId.trim()
        val email = command.email.trim()
        val name = command.name.trim()

        requireDomain(command.password.isNotBlank(), UserErrorCode.INVALID_PASSWORD)
        if (command.password != command.passwordConfirm) {
            throw GlobalException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH)
        }

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

    override fun requestLoginIdRecovery(command: RequestLoginIdRecoveryCommand): AccountRecoveryRequestResult {
        val name = command.name.trim()
        val email = command.email.trim()

        userRepository.findByNameAndEmail(name, email)
            ?.also { user ->
                accountRecoveryEmailSender.sendLoginIdRecoveryEmail(
                    email = user.email,
                    loginId = user.loginId,
                )
            }

        return AccountRecoveryRequestResult(accepted = true)
    }

    override fun requestPasswordReset(command: RequestPasswordResetCommand): AccountRecoveryRequestResult {
        val loginId = command.loginId.trim()
        val email = command.email.trim()
        val user = userRepository.findByLoginIdAndEmail(loginId, email)
            ?: return AccountRecoveryRequestResult(accepted = true)
        val now = LocalDateTime.now()
        val rawToken = accountRecoveryTokenGenerator.generate()
        val token = AccountRecoveryToken.create(
            id = idGenerator.generate(),
            userId = user.id,
            purpose = AccountRecoveryTokenPurpose.RESET_PASSWORD,
            tokenHash = accountRecoveryTokenGenerator.hash(rawToken),
            expiresAt = now.plus(PASSWORD_RESET_TOKEN_TTL),
        )

        accountRecoveryTokenRepository.markUnusedByUserIdAndPurposeAsUsed(
            userId = user.id,
            purpose = AccountRecoveryTokenPurpose.RESET_PASSWORD,
            usedAt = now,
        )
        val savedToken = accountRecoveryTokenRepository.save(token)
        accountRecoveryEmailSender.sendPasswordResetEmail(
            email = user.email,
            token = rawToken,
            expiresAt = savedToken.expiresAt,
        )

        return AccountRecoveryRequestResult(accepted = true)
    }

    override fun resetPassword(command: ResetPasswordCommand): ResetPasswordResult {
        if (command.newPassword != command.newPasswordConfirm) {
            throw GlobalException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH)
        }
        requireDomain(command.newPassword.isNotBlank(), UserErrorCode.INVALID_PASSWORD)

        val now = LocalDateTime.now()
        val tokenHash = accountRecoveryTokenGenerator.hash(command.token.trim())
        val token = accountRecoveryTokenRepository.findByTokenHashAndPurpose(
            tokenHash = tokenHash,
            purpose = AccountRecoveryTokenPurpose.RESET_PASSWORD,
        ) ?: throw GlobalException(AuthErrorCode.RESET_TOKEN_INVALID)
        val usedToken = token.use(now)
        val user = userRepository.findById(token.userId)
            ?: throw GlobalException(AuthErrorCode.RESET_TOKEN_INVALID)
        val passwordChangedUser = user.changePassword(passwordManager.encode(command.newPassword))

        userRepository.updatePassword(
            id = passwordChangedUser.id,
            encodedPassword = passwordChangedUser.encodedPassword,
        ) ?: throw GlobalException(AuthErrorCode.RESET_TOKEN_INVALID)
        accountRecoveryTokenRepository.save(usedToken)
        refreshTokenRepository.deleteByUserId(user.id)

        return ResetPasswordResult(reset = true)
    }

    private companion object {
        private val PASSWORD_RESET_TOKEN_TTL: Duration = Duration.ofMinutes(1)
    }
}
