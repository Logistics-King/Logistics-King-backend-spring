package logisticsking.com.logisticskingbackendspring.app.recommendation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.app.recommendation.dto.RecommendationResponse
import logisticsking.com.logisticskingbackendspring.app.recommendation.usecase.GetRecommendedAgenciesUseCase
import logisticsking.com.logisticskingbackendspring.app.recommendation.usecase.GetRecommendedVendorsUseCase
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Recommendation", description = "추천 API")
@SecurityRequirement(name = "accessTokenCookie")
@RestController
@RequestMapping("/api/v1/recommendations")
class RecommendationController(
    private val getRecommendedAgenciesUseCase: GetRecommendedAgenciesUseCase,
    private val getRecommendedVendorsUseCase: GetRecommendedVendorsUseCase,
) {

    @Operation(
        summary = "화주용 추천 대리점 조회",
        description = "로그인한 화주에게 이전 계약 이력과 지역 기준으로 추천 대리점 목록을 제공합니다.",
    )
    @EndpointAccess(roles = [UserRole.VENDOR])
    @GetMapping("/agencies")
    fun getRecommendedAgencies(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false, defaultValue = "10") limit: Int,
    ): ApiResponse<RecommendationResponse.RecommendedAgencies> {
        val results = getRecommendedAgenciesUseCase.getRecommendedAgencies(
            userId = user.userId,
            limit = limit,
        )

        return ApiResponse.success(
            response = RecommendationResponse.RecommendedAgencies.from(results),
        )
    }

    @Operation(
        summary = "대리점용 추천 화주 조회",
        description = "로그인한 대리점에게 이전 계약 이력과 지역 기준으로 추천 화주 목록을 제공합니다.",
    )
    @EndpointAccess(roles = [UserRole.AGENCY])
    @GetMapping("/vendors")
    fun getRecommendedVendors(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false, defaultValue = "10") limit: Int,
    ): ApiResponse<RecommendationResponse.RecommendedVendors> {
        val results = getRecommendedVendorsUseCase.getRecommendedVendors(
            userId = user.userId,
            limit = limit,
        )

        return ApiResponse.success(
            response = RecommendationResponse.RecommendedVendors.from(results),
        )
    }
}
