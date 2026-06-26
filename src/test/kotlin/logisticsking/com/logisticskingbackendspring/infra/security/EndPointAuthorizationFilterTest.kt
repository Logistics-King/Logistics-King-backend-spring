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
                    method = "GET",
                    roles = setOf(UserRole.ADMIN),
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
                    method = "GET",
                    roles = setOf(UserRole.VENDOR),
                    description = "vendor endpoints",
                )
            )
        )
        val response = MockHttpServletResponse()
        authenticate(UserRole.VENDOR)

        filter.doFilter(request("/api/v1/vendors/me"), response, MockFilterChain())

        assertEquals(200, response.status)
    }

    @Test
    fun `cors preflight 요청은 권한 검사를 건너뛴다`() {
        val filter = filterWith(emptyList())
        val response = MockHttpServletResponse()

        filter.doFilter(
            MockHttpServletRequest("OPTIONS", "/api/v1/vendors/me").apply {
                servletPath = "/api/v1/vendors/me"
                addHeader("Origin", "http://localhost:3000")
                addHeader("Access-Control-Request-Method", "GET")
            },
            response,
            MockFilterChain(),
        )

        assertEquals(200, response.status)
    }

    @Test
    fun `roles에 포함된 endpoint면 통과한다`() {
        val filter = filterWith(
            listOf(
                EndPoint.create(
                    url = "/api/v1/shared/**",
                    method = "GET",
                    roles = setOf(UserRole.VENDOR, UserRole.AGENCY),
                    description = "shared endpoints",
                )
            )
        )
        val response = MockHttpServletResponse()
        authenticate(UserRole.AGENCY)

        filter.doFilter(request("/api/v1/shared/me"), response, MockFilterChain())

        assertEquals(200, response.status)
    }

    @Test
    fun `권한 검사는 요청마다 repository를 다시 조회하지 않는다`() {
        val repository = CountingEndPointRepository(
            listOf(
                EndPoint.create(
                    url = "/api/v1/vendors/**",
                    method = "GET",
                    roles = setOf(UserRole.VENDOR),
                    description = "vendor endpoints",
                )
            )
        )
        val cache = EndPointAuthorizationCache(repository)
        cache.reload()
        val filter = EndPointAuthorizationFilter(
            endPointAuthorizationCache = cache,
            securityResponseWriter = SecurityResponseWriter(ObjectMapper()),
        )
        authenticate(UserRole.VENDOR)

        filter.doFilter(request("/api/v1/vendors/me"), MockHttpServletResponse(), MockFilterChain())
        filter.doFilter(request("/api/v1/vendors/me"), MockHttpServletResponse(), MockFilterChain())

        assertEquals(1, repository.findAllCount)
    }

    private fun filterWith(endPoints: List<EndPoint>): EndPointAuthorizationFilter {
        val repository = FakeEndPointRepository(endPoints)
        val cache = EndPointAuthorizationCache(repository)
        cache.reload()

        return EndPointAuthorizationFilter(
            endPointAuthorizationCache = cache,
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
        override fun findAll(): List<EndPoint> {
            return endPoints
        }

        override fun findByUrlAndMethod(url: String, method: String): EndPoint? {
            return endPoints.firstOrNull { it.url == url && it.method == method }
        }

        override fun save(endPoint: EndPoint): EndPoint {
            return endPoint
        }
    }

    private class CountingEndPointRepository(
        private val endPoints: List<EndPoint>,
    ) : EndPointRepository {
        var findAllCount: Int = 0
            private set

        override fun findAll(): List<EndPoint> {
            findAllCount += 1
            return endPoints
        }

        override fun findByUrlAndMethod(url: String, method: String): EndPoint? {
            return endPoints.firstOrNull { it.url == url && it.method == method }
        }

        override fun save(endPoint: EndPoint): EndPoint {
            return endPoint
        }
    }
}
