package logisticsking.com.logisticskingbackendspring.infra.security

import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheChangedResult
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheDryRunResult
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheReloadResult
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheSnapshotResult
import logisticsking.com.logisticskingbackendspring.app.permission.usecase.DryRunEndPointAuthorizationCacheUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.usecase.ReloadEndPointAuthorizationCacheUseCase
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

@Component
class EndPointAuthorizationCache(
    private val endPointRepository: EndPointRepository,
) : DryRunEndPointAuthorizationCacheUseCase,
    ReloadEndPointAuthorizationCacheUseCase {

    private val pathMatcher = AntPathMatcher()
    private val endPointsByMethod = AtomicReference<Map<String, List<EndPoint>>>(emptyMap())

    override fun reload(): EndPointAuthorizationCacheReloadResult {
        val databaseEndPoints = endPointRepository.findAll()
        val nextCache = databaseEndPoints.groupBy { endPoint -> endPoint.method }

        endPointsByMethod.set(nextCache)

        return EndPointAuthorizationCacheReloadResult(
            cacheCount = databaseEndPoints.size,
            reloadedAt = Instant.now(),
        )
    }

    override fun dryRun(): EndPointAuthorizationCacheDryRunResult {
        val cacheSnapshots = currentEndPoints()
            .map { endPoint -> endPoint.toSnapshotResult() }
            .associateBy { snapshot -> snapshot.key }
        val databaseSnapshots = endPointRepository.findAll()
            .map { endPoint -> endPoint.toSnapshotResult() }
            .associateBy { snapshot -> snapshot.key }

        val added = databaseSnapshots
            .filterKeys { key -> key !in cacheSnapshots }
            .values
            .sorted()
        val removed = cacheSnapshots
            .filterKeys { key -> key !in databaseSnapshots }
            .values
            .sorted()
        val changed = databaseSnapshots
            .mapNotNull { (key, after) ->
                val before = cacheSnapshots[key] ?: return@mapNotNull null
                if (before == after) {
                    null
                } else {
                    EndPointAuthorizationCacheChangedResult(
                        before = before,
                        after = after,
                    )
                }
            }
            .sortedWith(
                compareBy<EndPointAuthorizationCacheChangedResult> { changedResult -> changedResult.after.method }
                    .thenBy { changedResult -> changedResult.after.url }
            )

        return EndPointAuthorizationCacheDryRunResult(
            cacheCount = cacheSnapshots.size,
            databaseCount = databaseSnapshots.size,
            added = added,
            removed = removed,
            changed = changed,
        )
    }

    fun isAllowed(
        requestUri: String,
        requestMethod: String,
        role: UserRole,
    ): Boolean {
        return endPointsByMethod.get()
            .orEmpty()[requestMethod]
            .orEmpty()
            .any { endPoint ->
                pathMatcher.match(endPoint.url, requestUri) &&
                    endPoint.allows(role)
            }
    }

    private fun currentEndPoints(): List<EndPoint> {
        return endPointsByMethod.get()
            .values
            .flatten()
    }

    private fun EndPoint.toSnapshotResult(): EndPointAuthorizationCacheSnapshotResult {
        return EndPointAuthorizationCacheSnapshotResult(
            url = url,
            method = method,
            roles = roles.map { role -> role.name }.sorted(),
            description = description,
        )
    }

    private val EndPointAuthorizationCacheSnapshotResult.key: EndPointCacheKey
        get() = EndPointCacheKey(
            url = url,
            method = method,
        )

    private fun Collection<EndPointAuthorizationCacheSnapshotResult>.sorted(): List<EndPointAuthorizationCacheSnapshotResult> {
        return sortedWith(
            compareBy<EndPointAuthorizationCacheSnapshotResult> { snapshot -> snapshot.method }
                .thenBy { snapshot -> snapshot.url }
        )
    }

    private data class EndPointCacheKey(
        val url: String,
        val method: String,
    )
}
