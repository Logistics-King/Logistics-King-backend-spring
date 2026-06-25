package logisticsking.com.logisticskingbackendspring.infra.security

import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EndPointAuthorizationCacheTest {

    @Test
    fun `dry-run은 DB와 캐시의 차이를 반환하고 캐시는 변경하지 않는다`() {
        val repository = MutableEndPointRepository(
            listOf(
                endPoint(
                    url = "/api/v1/vendors/me",
                    roles = setOf(UserRole.ADMIN, UserRole.VENDOR),
                )
            )
        )
        val cache = EndPointAuthorizationCache(repository)
        cache.reload()

        repository.endPoints = listOf(
            endPoint(
                url = "/api/v1/vendors/me",
                roles = setOf(UserRole.ADMIN, UserRole.AGENCY),
            ),
            endPoint(
                url = "/api/v1/agencies/me",
                roles = setOf(UserRole.ADMIN, UserRole.AGENCY),
            ),
        )

        val result = cache.dryRun()

        assertTrue(result.hasChanges)
        assertEquals(1, result.cacheCount)
        assertEquals(2, result.databaseCount)
        assertEquals(1, result.added.size)
        assertEquals("/api/v1/agencies/me", result.added.first().url)
        assertEquals(1, result.changed.size)
        assertEquals(listOf("ADMIN", "VENDOR"), result.changed.first().before.roles)
        assertEquals(listOf("ADMIN", "AGENCY"), result.changed.first().after.roles)
        assertTrue(cache.isAllowed("/api/v1/vendors/me", "GET", UserRole.VENDOR))
        assertFalse(cache.isAllowed("/api/v1/agencies/me", "GET", UserRole.AGENCY))
    }

    @Test
    fun `reload는 DB 기준으로 캐시를 갱신한다`() {
        val repository = MutableEndPointRepository(
            listOf(
                endPoint(
                    url = "/api/v1/vendors/me",
                    roles = setOf(UserRole.ADMIN, UserRole.VENDOR),
                )
            )
        )
        val cache = EndPointAuthorizationCache(repository)
        cache.reload()
        repository.endPoints = listOf(
            endPoint(
                url = "/api/v1/agencies/me",
                roles = setOf(UserRole.ADMIN, UserRole.AGENCY),
            )
        )

        val reloadResult = cache.reload()
        val dryRunResult = cache.dryRun()

        assertEquals(1, reloadResult.cacheCount)
        assertFalse(dryRunResult.hasChanges)
        assertFalse(cache.isAllowed("/api/v1/vendors/me", "GET", UserRole.VENDOR))
        assertTrue(cache.isAllowed("/api/v1/agencies/me", "GET", UserRole.AGENCY))
    }

    private fun endPoint(
        url: String,
        roles: Set<UserRole>,
        method: String = "GET",
        description: String = "test endpoint",
    ): EndPoint {
        return EndPoint.create(
            url = url,
            method = method,
            roles = roles,
            description = description,
        )
    }

    private class MutableEndPointRepository(
        var endPoints: List<EndPoint>,
    ) : EndPointRepository {
        override fun findAll(): List<EndPoint> {
            return endPoints
        }

        override fun findByUrlAndMethod(url: String, method: String): EndPoint? {
            return endPoints.firstOrNull { endPoint -> endPoint.url == url && endPoint.method == method }
        }

        override fun save(endPoint: EndPoint): EndPoint {
            endPoints = endPoints + endPoint
            return endPoint
        }
    }
}
