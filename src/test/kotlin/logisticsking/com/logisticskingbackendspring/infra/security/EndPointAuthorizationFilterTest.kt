package logisticsking.com.logisticskingbackendspring.infra.security

import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import tools.jackson.databind.ObjectMapper
import java.util.UUID

class EndPointAuthorizationFilterTest {

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `인증 정보가 없으면 401을 응답한다`() {
        val filter = filterWith(emptyList())
        val response = MockHttpServletResponse()

        filter.doFilter(request("/api/v1/users"), response, MockFilterChain())

        assertEquals(401, response.status)
    }

    @Test
    fun `role에 허용된 endpoint가 없으면 403을 응답한다`() {
        val filter = filterWith(
            listOf(
                EndPoint.create(
                    url = "/api/v1/admin/**",
                    role = UserRole.ADMIN,
                    description = "admin only",
                )
            )
        )
        val response = MockHttpServletResponse()
        authenticate(UserRole.VENDOR)

        filter.doFilter(request("/api/v1/admin/users"), response, MockFilterChain())

        assertEquals(403, response.status)
    }

    @Test
    fun `role에 허용된 endpoint면 통과한다`() {
        val filter = filterWith(
            listOf(
                EndPoint.create(
                    url = "/api/v1/vendors/**",
                    role = UserRole.VENDOR,
                    description = "vendor endpoints",
                )
            )
        )
        val response = MockHttpServletResponse()
        authenticate(UserRole.VENDOR)

        filter.doFilter(request("/api/v1/vendors/me"), response, MockFilterChain())

        assertEquals(200, response.status)
    }

    private fun filterWith(endPoints: List<EndPoint>): EndPointAuthorizationFilter {
        return EndPointAuthorizationFilter(
            endPointRepository = FakeEndPointRepository(endPoints),
            securityResponseWriter = SecurityResponseWriter(ObjectMapper()),
        )
    }

    private fun request(path: String): MockHttpServletRequest {
        return MockHttpServletRequest("GET", path).apply {
            servletPath = path
        }
    }

    private fun authenticate(role: UserRole) {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            AuthenticatedUser(
                userId = UUID.randomUUID(),
                role = role,
            ),
            null,
            emptyList(),
        )
    }

    private class FakeEndPointRepository(
        private val endPoints: List<EndPoint>,
    ) : EndPointRepository {
        override fun findByRole(role: UserRole): List<EndPoint> {
            return endPoints.filter { it.role == role }
        }

        override fun existsByUrlAndRole(
            url: String,
            role: UserRole,
        ): Boolean {
            return endPoints.any { it.url == url && it.role == role }
        }

        override fun save(endPoint: EndPoint): EndPoint {
            return endPoint
        }
    }
}
