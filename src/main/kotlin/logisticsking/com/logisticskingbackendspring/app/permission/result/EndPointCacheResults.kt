package logisticsking.com.logisticskingbackendspring.app.permission.result

import java.time.Instant

data class EndPointAuthorizationCacheDryRunResult(
    val cacheCount: Int,
    val databaseCount: Int,
    val added: List<EndPointAuthorizationCacheSnapshotResult>,
    val removed: List<EndPointAuthorizationCacheSnapshotResult>,
    val changed: List<EndPointAuthorizationCacheChangedResult>,
) {
    val hasChanges: Boolean = added.isNotEmpty() || removed.isNotEmpty() || changed.isNotEmpty()
}

data class EndPointAuthorizationCacheReloadResult(
    val cacheCount: Int,
    val reloadedAt: Instant,
)

data class EndPointAuthorizationCacheSnapshotResult(
    val url: String,
    val method: String,
    val roles: List<String>,
    val description: String?,
)

data class EndPointAuthorizationCacheChangedResult(
    val before: EndPointAuthorizationCacheSnapshotResult,
    val after: EndPointAuthorizationCacheSnapshotResult,
)
