package logisticsking.com.logisticskingbackendspring.infra.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TokenCookieManager(
    @Value("\${auth.cookie.access-token-name}") private val accessTokenName: String,
    @Value("\${auth.cookie.refresh-token-name}") private val refreshTokenName: String,
    @Value("\${auth.cookie.path}") private val path: String,
    @Value("\${auth.cookie.http-only}") private val httpOnly: Boolean,
    @Value("\${auth.cookie.secure}") private val secure: Boolean,
    @Value("\${auth.cookie.same-site}") private val sameSite: String,
    @Value("\${auth.jwt.access-token-expiration-seconds}") private val accessTokenExpirationSeconds: Long,
    @Value("\${auth.jwt.refresh-token-expiration-seconds}") private val refreshTokenExpirationSeconds: Long,
) {

    fun createAccessTokenCookie(token: String): ResponseCookie {
        return createCookie(
            name = accessTokenName,
            value = token,
            maxAge = Duration.ofSeconds(accessTokenExpirationSeconds),
        )
    }

    fun createRefreshTokenCookie(token: String): ResponseCookie {
        return createCookie(
            name = refreshTokenName,
            value = token,
            maxAge = Duration.ofSeconds(refreshTokenExpirationSeconds),
        )
    }

    fun expireAccessTokenCookie(): ResponseCookie {
        return createCookie(
            name = accessTokenName,
            value = "",
            maxAge = Duration.ZERO,
        )
    }

    fun expireRefreshTokenCookie(): ResponseCookie {
        return createCookie(
            name = refreshTokenName,
            value = "",
            maxAge = Duration.ZERO,
        )
    }

    fun extractAccessToken(request: HttpServletRequest): String? {
        return extractCookieValue(request, accessTokenName)
    }

    fun extractRefreshToken(request: HttpServletRequest): String? {
        return extractCookieValue(request, refreshTokenName)
    }

    private fun extractCookieValue(
        request: HttpServletRequest,
        name: String,
    ): String? {
        return request.cookies
            ?.firstOrNull { it.name == name }
            ?.value
            ?.takeIf { it.isNotBlank() }
    }

    private fun createCookie(
        name: String,
        value: String,
        maxAge: Duration,
    ): ResponseCookie {
        return ResponseCookie.from(name, value)
            .httpOnly(httpOnly)
            .secure(secure)
            .sameSite(sameSite)
            .path(path)
            .maxAge(maxAge)
            .build()
    }
}
