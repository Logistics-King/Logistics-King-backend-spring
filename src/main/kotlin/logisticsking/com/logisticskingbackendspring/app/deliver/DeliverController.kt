package logisticsking.com.logisticskingbackendspring.app.deliver

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.deliver.dto.DeliverRequest
import logisticsking.com.logisticskingbackendspring.app.deliver.dto.DeliverResponse
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.CreateDeliverUseCase
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.GetAgencyDeliversUseCase
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.GetMyDeliverUseCase
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.UpdateDeliverUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Deliver", description = "배송기사 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.ADMIN, UserRole.DRIVER])
@RestController
@RequestMapping("/api/v1/delivers")
class DeliverController(
    private val createDeliverUseCase: CreateDeliverUseCase,
    private val getMyDeliverUseCase: GetMyDeliverUseCase,
    private val getAgencyDeliversUseCase: GetAgencyDeliversUseCase,
    private val updateDeliverUseCase: UpdateDeliverUseCase,
) {

    @Operation(summary = "내 배송기사 프로필 생성", description = "로그인한 배송기사 사용자의 프로필을 생성합니다.")
    @PostMapping("/me")
    fun createMyDeliver(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: DeliverRequest.Create,
    ): ApiResponse<DeliverResponse.Detail> {
        val result = createDeliverUseCase.create(request.toCommand(user.userId))

        return ApiResponse.success(
            response = DeliverResponse.Detail.from(result),
        )
    }

    @Operation(summary = "내 배송기사 프로필 조회", description = "로그인한 배송기사 사용자의 프로필을 조회합니다.")
    @GetMapping("/me")
    fun getMyDeliver(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<DeliverResponse.Detail> {
        val result = getMyDeliverUseCase.getMyDeliver(user.userId)

        return ApiResponse.success(
            response = DeliverResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "대리점 배송기사 목록 조회", description = "로그인한 대리점에 소속된 배송기사 목록을 조회합니다.")
    @GetMapping("/agency/me")
    fun getAgencyDelivers(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DeliverResponse.List> {
        val results = getAgencyDeliversUseCase.getAgencyDelivers(user.userId, pageable)

        return ApiResponse.success(
            response = DeliverResponse.List.from(results),
        )
    }

    @Operation(summary = "내 배송기사 프로필 수정", description = "로그인한 배송기사 사용자의 프로필을 수정합니다.")
    @PutMapping("/me")
    fun updateMyDeliver(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: DeliverRequest.Update,
    ): ApiResponse<DeliverResponse.Detail> {
        val result = updateDeliverUseCase.update(request.toCommand(user.userId))

        return ApiResponse.success(
            response = DeliverResponse.Detail.from(result),
        )
    }
}
