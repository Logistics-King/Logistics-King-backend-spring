package logisticsking.com.logisticskingbackendspring.app.permission.usecase

import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheDryRunResult
import logisticsking.com.logisticskingbackendspring.app.permission.result.EndPointAuthorizationCacheReloadResult

interface DryRunEndPointAuthorizationCacheUseCase {
    fun dryRun(): EndPointAuthorizationCacheDryRunResult
}

interface ReloadEndPointAuthorizationCacheUseCase {
    fun reload(): EndPointAuthorizationCacheReloadResult
}
