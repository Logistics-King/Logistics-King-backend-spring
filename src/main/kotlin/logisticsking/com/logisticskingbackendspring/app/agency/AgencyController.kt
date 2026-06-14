package logisticsking.com.logisticskingbackendspring.app.agency

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.agency.dto.AgencyRequest
import logisticsking.com.logisticskingbackendspring.app.agency.dto.AgencyResponse
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.CreateAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.GetAgenciesUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.GetAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.GetMyAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.UpdateAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "Agency", description = "대리점 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.ADMIN, UserRole.AGENCY])
@RestController
@RequestMapping("/api/v1/agencies")
class AgencyController(
    private val createAgencyUseCase: CreateAgencyUseCase,
    private val getMyAgencyUseCase: GetMyAgencyUseCase,
    private val getAgenciesUseCase: GetAgenciesUseCase,
    private val getAgencyUseCase: GetAgencyUseCase,
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

    @EndpointAccess(roles = [UserRole.VENDOR])
    @Operation(summary = "근방 대리점 목록 조회", description = "화주가 계약 요청을 보낼 수 있는 근방 대리점 목록을 조회합니다.")
    @GetMapping
    fun getAgencies(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false) region: String?,
        @RequestParam(required = false) carrier: Carrier?,
        @RequestParam(required = false) saturdayDeliveryAvailable: Boolean?,
        @RequestParam(required = false) returnAvailable: Boolean?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<AgencyResponse.List> {
        val results = getAgenciesUseCase.getAgencies(
            userId = user.userId,
            condition = AgencySearchCondition(
                region = region,
                carrier = carrier,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
            ),
            pageable = pageable,
        )

        return ApiResponse.success(
            response = AgencyResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.VENDOR])
    @Operation(summary = "대리점 상세 조회", description = "화주가 계약 요청을 보낼 대리점 상세 정보를 조회합니다.")
    @GetMapping("/{agencyId}")
    fun getAgency(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable agencyId: UUID,
    ): ApiResponse<AgencyResponse.Detail> {
        val result = getAgencyUseCase.getAgency(
            userId = user.userId,
            agencyId = agencyId,
        )

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
