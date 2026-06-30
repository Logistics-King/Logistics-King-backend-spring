package logisticsking.com.logisticskingbackendspring.infra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import logisticsking.com.logisticskingbackendspring.domain.auth.AuthErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class EndPointAuthorizationFilter(
    private val endPointAuthorizationCache: EndPointAuthorizationCache,
    private val securityResponseWriter: SecurityResponseWriter,
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return CorsUtils.isPreFlightRequest(request) ||
            !request.servletPath.startsWith(API_PATH) ||
            PUBLIC_PATHS.any { publicPath -> request.servletPath.startsWith(publicPath) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val principal = SecurityContextHolder.getContext().authentication?.principal as? AuthenticatedUser
        if (principal == null) {
            securityResponseWriter.writeError(response, AuthErrorCode.UNAUTHORIZED)
            return
        }

        val requestUri = request.servletPath
        val requestMethod = request.method
        val allowed = endPointAuthorizationCache.isAllowed(
            requestUri = requestUri,
            requestMethod = requestMethod,
            role = principal.role,
        )

        if (!allowed) {
            securityResponseWriter.writeError(response, AuthErrorCode.FORBIDDEN)
            return
        }

        filterChain.doFilter(request, response)
    }

    private companion object {
        private const val API_PATH = "/api/v1/"
        private val PUBLIC_PATHS = listOf(
            "/api/v1/auth/",
        )
    }
}
