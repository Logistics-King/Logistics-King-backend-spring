package logisticsking.com.logisticskingbackendspring.infra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import logisticsking.com.logisticskingbackendspring.domain.auth.AuthErrorCode
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
class EndPointAuthorizationFilter(
    private val endPointRepository: EndPointRepository,
    private val securityResponseWriter: SecurityResponseWriter,
) : OncePerRequestFilter() {

    private val pathMatcher = AntPathMatcher()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.servletPath.startsWith(API_PATH) ||
            request.servletPath.startsWith(AUTH_PATH)
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
        val allowed = endPointRepository.findByRole(principal.role)
            .any { endPoint -> pathMatcher.match(endPoint.url, requestUri) }

        if (!allowed) {
            securityResponseWriter.writeError(response, AuthErrorCode.FORBIDDEN)
            return
        }

        filterChain.doFilter(request, response)
    }

    private companion object {
        private const val API_PATH = "/api/v1/"
        private const val AUTH_PATH = "/api/v1/auth/"
    }
}
