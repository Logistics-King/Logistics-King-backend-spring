package logisticsking.com.logisticskingbackendspring.infra.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import logisticsking.com.logisticskingbackendspring.domain.auth.AuthErrorCode
import logisticsking.com.logisticskingbackendspring.domain.auth.TokenClaims
import logisticsking.com.logisticskingbackendspring.domain.auth.TokenProvider
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import java.util.UUID

@Component
class JwtTokenProvider(
    @Value("\${auth.jwt.secret}") private val secret: String,
    @Value("\${auth.jwt.access-token-expiration-seconds}") private val accessTokenExpirationSeconds: Long,
    @Value("\${auth.jwt.refresh-token-expiration-seconds}") private val refreshTokenExpirationSeconds: Long,
) : TokenProvider {

    private val algorithm: Algorithm by lazy { Algorithm.HMAC256(secret) }

    override fun generateAccessToken(user: User): String {
        return generateToken(
            user = user,
            tokenType = ACCESS_TOKEN_TYPE,
            expirationSeconds = accessTokenExpirationSeconds,
        )
    }

    override fun generateRefreshToken(user: User): String {
        return generateToken(
            user = user,
            tokenType = REFRESH_TOKEN_TYPE,
            expirationSeconds = refreshTokenExpirationSeconds,
        )
    }

    override fun parseAccessToken(token: String): TokenClaims {
        return parseToken(token, ACCESS_TOKEN_TYPE)
    }

    override fun parseRefreshToken(token: String): TokenClaims {
        return parseToken(token, REFRESH_TOKEN_TYPE)
    }

    private fun generateToken(
        user: User,
        tokenType: String,
        expirationSeconds: Long,
    ): String {
        val issuedAt = Instant.now()
        val expiresAt = issuedAt.plusSeconds(expirationSeconds)

        return JWT.create()
            .withSubject(user.id.toString())
            .withClaim(ROLE_CLAIM, user.role.name)
            .withClaim(TYPE_CLAIM, tokenType)
            .withIssuedAt(Date.from(issuedAt))
            .withExpiresAt(Date.from(expiresAt))
            .sign(algorithm)
    }

    private fun parseToken(
        token: String,
        expectedTokenType: String,
    ): TokenClaims {
        try {
            val decoded = JWT.require(algorithm).build().verify(token)
            val tokenType = decoded.getClaim(TYPE_CLAIM).asString()
            if (tokenType != expectedTokenType) {
                throw GlobalException(AuthErrorCode.INVALID_TOKEN)
            }

            return TokenClaims(
                userId = UUID.fromString(decoded.subject),
                role = UserRole.valueOf(decoded.getClaim(ROLE_CLAIM).asString()),
                expiresAt = decoded.expiresAt.toInstant(),
            )
        } catch (exception: IllegalArgumentException) {
            throw GlobalException(AuthErrorCode.INVALID_TOKEN)
        } catch (exception: JWTVerificationException) {
            throw GlobalException(AuthErrorCode.INVALID_TOKEN)
        }
    }

    private companion object {
        private const val ROLE_CLAIM = "role"
        private const val TYPE_CLAIM = "type"
        private const val ACCESS_TOKEN_TYPE = "ACCESS"
        private const val REFRESH_TOKEN_TYPE = "REFRESH"
    }
}
