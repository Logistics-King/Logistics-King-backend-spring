package logisticsking.com.logisticskingbackendspring.infra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import logisticsking.com.logisticskingbackendspring.domain.auth.AuthErrorCode
import logisticsking.com.logisticskingbackendspring.domain.auth.TokenProvider
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val tokenCookieManager: TokenCookieManager,
    private val securityResponseWriter: SecurityResponseWriter,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath.startsWith(AUTH_PATH)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val accessToken = tokenCookieManager.extractAccessToken(request)
        if (accessToken == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val claims = tokenProvider.parseAccessToken(accessToken)
            val principal = AuthenticatedUser(
                userId = claims.userId,
                role = claims.role,
            )
            SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                principal,
                null,
                listOf(SimpleGrantedAuthority("ROLE_${claims.role.name}")),
            )
            filterChain.doFilter(request, response)
        } catch (exception: GlobalException) {
            SecurityContextHolder.clearContext()
            securityResponseWriter.writeError(response, AuthErrorCode.INVALID_TOKEN)
        }
    }

    private companion object {
        private const val AUTH_PATH = "/api/v1/auth/"
    }
}
