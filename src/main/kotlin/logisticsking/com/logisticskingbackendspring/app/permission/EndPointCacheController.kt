package logisticsking.com.logisticskingbackendspring.app.permission

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.permission.dto.EndPointCacheResponse
import logisticsking.com.logisticskingbackendspring.app.permission.usecase.DryRunEndPointAuthorizationCacheUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.usecase.ReloadEndPointAuthorizationCacheUseCase
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "EndPoint Cache", description = "API 권한 캐시 운영 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.ADMIN])
@RestController
@RequestMapping("/api/v1/admin/end-points/cache")
class EndPointCacheController(
    private val dryRunEndPointAuthorizationCacheUseCase: DryRunEndPointAuthorizationCacheUseCase,
    private val reloadEndPointAuthorizationCacheUseCase: ReloadEndPointAuthorizationCacheUseCase,
) {

    @Operation(
        summary = "API 권한 캐시 갱신 dry-run",
        description = "DB end_points 정책과 현재 메모리 권한 캐시의 차이를 조회합니다. 실제 캐시는 변경하지 않습니다.",
    )
    @GetMapping("/dry-run")
    fun dryRun(): ApiResponse<EndPointCacheResponse.DryRun> {
        val result = dryRunEndPointAuthorizationCacheUseCase.dryRun()

        return ApiResponse.success(
            response = EndPointCacheResponse.DryRun.from(result),
        )
    }

    @Operation(
        summary = "API 권한 캐시 갱신",
        description = "DB end_points 정책을 다시 읽어 메모리 권한 캐시를 갱신합니다.",
    )
    @PostMapping("/reload")
    fun reload(): ApiResponse<EndPointCacheResponse.Reload> {
        val result = reloadEndPointAuthorizationCacheUseCase.reload()

        return ApiResponse.success(
            response = EndPointCacheResponse.Reload.from(result),
        )
    }
}
