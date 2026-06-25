package logisticsking.com.logisticskingbackendspring.app.permission.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheChangedResult
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheDryRunResult
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheReloadResult
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheSnapshotResult
import java.time.Instant

sealed interface EndPointCacheResponse {
    @Schema(description = "API 권한 캐시 갱신 dry-run 응답")
    data class DryRun(
        @field:Schema(description = "현재 캐시 endpoint 수", example = "52")
        val cacheCount: Int,

        @field:Schema(description = "DB endpoint 수", example = "53")
        val databaseCount: Int,

        @field:Schema(description = "DB와 캐시 사이 변경 여부", example = "true")
        val hasChanges: Boolean,

        @field:Schema(description = "reload 시 새로 추가될 endpoint 목록")
        val added: List<Snapshot>,

        @field:Schema(description = "reload 시 캐시에서 제거될 endpoint 목록")
        val removed: List<Snapshot>,

        @field:Schema(description = "reload 시 roles 또는 description이 변경될 endpoint 목록")
        val changed: List<Changed>,
    ) : EndPointCacheResponse {
        companion object {
            fun from(result: EndPointAuthorizationCacheDryRunResult): DryRun {
                return DryRun(
                    cacheCount = result.cacheCount,
                    databaseCount = result.databaseCount,
                    hasChanges = result.hasChanges,
                    added = result.added.map(Snapshot::from),
                    removed = result.removed.map(Snapshot::from),
                    changed = result.changed.map(Changed::from),
                )
            }
        }
    }

    @Schema(description = "API 권한 캐시 갱신 응답")
    data class Reload(
        @field:Schema(description = "갱신 후 캐시에 적재된 endpoint 수", example = "53")
        val cacheCount: Int,

        @field:Schema(description = "갱신 시각", example = "2026-06-25T10:30:00Z")
        val reloadedAt: Instant,
    ) : EndPointCacheResponse {
        companion object {
            fun from(result: EndPointAuthorizationCacheReloadResult): Reload {
                return Reload(
                    cacheCount = result.cacheCount,
                    reloadedAt = result.reloadedAt,
                )
            }
        }
    }

    @Schema(description = "API 권한 캐시 endpoint snapshot")
    data class Snapshot(
        @field:Schema(description = "API URL pattern", example = "/api/v1/vendors/me")
        val url: String,

        @field:Schema(description = "HTTP method", example = "GET")
        val method: String,

        @field:Schema(description = "허용 role 목록", example = "[\"ADMIN\", \"VENDOR\"]")
        val roles: List<String>,

        @field:Schema(description = "API 설명", example = "로그인한 화주 사용자의 사업 프로필을 조회합니다.")
        val description: String?,
    ) {
        companion object {
            fun from(result: EndPointAuthorizationCacheSnapshotResult): Snapshot {
                return Snapshot(
                    url = result.url,
                    method = result.method,
                    roles = result.roles,
                    description = result.description,
                )
            }
        }
    }

    @Schema(description = "API 권한 캐시 변경 endpoint")
    data class Changed(
        @field:Schema(description = "현재 캐시 값")
        val before: Snapshot,

        @field:Schema(description = "DB 기준 값")
        val after: Snapshot,
    ) {
        companion object {
            fun from(result: EndPointAuthorizationCacheChangedResult): Changed {
                return Changed(
                    before = Snapshot.from(result.before),
                    after = Snapshot.from(result.after),
                )
            }
        }
    }
}
