package logisticsking.com.logisticskingbackendspring.infra.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestLoggingFilter(
    private val requestIdGenerator: RequestIdGenerator,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val startedAt = System.nanoTime()
        val requestId = requestIdGenerator.next().toString()

        MDC.put(REQUEST_ID, requestId)
        MDC.put(METHOD, request.method)
        MDC.put(PATH, request.servletPath)
        response.setHeader(REQUEST_ID_HEADER, requestId)

        try {
            filterChain.doFilter(request, response)
        } finally {
            putAuthenticatedUserMdc()
            logCompletedRequest(
                request = request,
                response = response,
                latencyMs = (System.nanoTime() - startedAt) / NANO_TO_MILLIS,
            )
            MDC.clear()
        }
    }

    private fun putAuthenticatedUserMdc() {
        val principal = SecurityContextHolder.getContext().authentication?.principal as? AuthenticatedUser ?: return

        MDC.put(USER_ID, principal.userId.toString())
        MDC.put(USER_ROLE, principal.role.name)
    }

    private fun logCompletedRequest(
        request: HttpServletRequest,
        response: HttpServletResponse,
        latencyMs: Long,
    ) {
        val status = response.status
        val queryString = request.queryString?.let { query -> "?$query" }.orEmpty()
        val message = "HTTP request completed method=${request.method} path=${request.servletPath}$queryString status=$status latencyMs=$latencyMs clientIp=${request.clientIp()} userAgent=\"${request.userAgent()}\""

        when {
            status >= SERVER_ERROR_STATUS -> requestLogger.error(message)
            status >= CLIENT_ERROR_STATUS -> requestLogger.warn(message)
            else -> requestLogger.info(message)
        }
    }

    private fun HttpServletRequest.clientIp(): String {
        return getHeader(FORWARDED_FOR_HEADER)
            ?.split(",")
            ?.firstOrNull()
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?: remoteAddr
            ?: UNKNOWN
    }

    private fun HttpServletRequest.userAgent(): String {
        return getHeader(USER_AGENT_HEADER)
            ?.take(MAX_USER_AGENT_LENGTH)
            ?: UNKNOWN
    }

    private companion object {
        private const val REQUEST_ID = "requestId"
        private const val METHOD = "method"
        private const val PATH = "path"
        private const val USER_ID = "userId"
        private const val USER_ROLE = "userRole"
        private const val REQUEST_ID_HEADER = "X-Request-Id"
        private const val FORWARDED_FOR_HEADER = "X-Forwarded-For"
        private const val USER_AGENT_HEADER = "User-Agent"
        private const val UNKNOWN = "-"
        private const val CLIENT_ERROR_STATUS = 400
        private const val SERVER_ERROR_STATUS = 500
        private const val MAX_USER_AGENT_LENGTH = 300
        private const val NANO_TO_MILLIS = 1_000_000L
        private val requestLogger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)
    }
}
