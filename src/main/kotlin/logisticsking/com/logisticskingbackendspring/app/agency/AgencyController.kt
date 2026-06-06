package logisticsking.com.logisticskingbackendspring.app.agency

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.agency.dto.AgencyRequest
import logisticsking.com.logisticskingbackendspring.app.agency.dto.AgencyResponse
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.CreateAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.GetMyAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.UpdateAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Agency", description = "대리점 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.ADMIN, UserRole.AGENCY])
@RestController
@RequestMapping("/api/v1/agencies")
class AgencyController(
    private val createAgencyUseCase: CreateAgencyUseCase,
    private val getMyAgencyUseCase: GetMyAgencyUseCase,
    private val updateAgencyUseCase: UpdateAgencyUseCase,
) {

    @Operation(summary = "내 대리점 프로필 생성", description = "로그인한 대리점 사용자의 영업 거점 프로필을 생성합니다.")
    @PostMapping("/me")
    fun createMyAgency(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: AgencyRequest.Create,
    ): ApiResponse<AgencyResponse.Detail> {
        val result = createAgencyUseCase.create(request.toCommand(user.userId))

        return ApiResponse.success(
            response = AgencyResponse.Detail.from(result),
        )
    }

    @Operation(summary = "내 대리점 프로필 조회", description = "로그인한 대리점 사용자의 영업 거점 프로필을 조회합니다.")
    @GetMapping("/me")
    fun getMyAgency(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<AgencyResponse.Detail> {
        val result = getMyAgencyUseCase.getMyAgency(user.userId)

        return ApiResponse.success(
            response = AgencyResponse.Detail.from(result),
        )
    }

    @Operation(summary = "내 대리점 프로필 수정", description = "로그인한 대리점 사용자의 영업 거점 프로필을 수정합니다.")
    @PutMapping("/me")
    fun updateMyAgency(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: AgencyRequest.Update,
    ): ApiResponse<AgencyResponse.Detail> {
        val result = updateAgencyUseCase.update(request.toCommand(user.userId))

        return ApiResponse.success(
            response = AgencyResponse.Detail.from(result),
        )
    }
}
