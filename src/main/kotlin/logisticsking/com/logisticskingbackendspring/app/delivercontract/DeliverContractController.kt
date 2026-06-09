package logisticsking.com.logisticskingbackendspring.app.delivercontract

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.DeliverContractIdCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.dto.DeliverContractRequest
import logisticsking.com.logisticskingbackendspring.app.delivercontract.dto.DeliverContractResponse
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.AcceptDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.CancelDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.CreateDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.GetMyAgencyDeliverContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.GetMyDriverDeliverContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.RejectDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.UpdateDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
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
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "DeliverContract", description = "배송기사 계약 API")
@SecurityRequirement(name = "accessTokenCookie")
@RestController
@RequestMapping("/api/v1/deliver-contracts")
class DeliverContractController(
    private val createDeliverContractUseCase: CreateDeliverContractUseCase,
    private val getMyAgencyDeliverContractsUseCase: GetMyAgencyDeliverContractsUseCase,
    private val getMyDriverDeliverContractsUseCase: GetMyDriverDeliverContractsUseCase,
    private val updateDeliverContractUseCase: UpdateDeliverContractUseCase,
    private val acceptDeliverContractUseCase: AcceptDeliverContractUseCase,
    private val rejectDeliverContractUseCase: RejectDeliverContractUseCase,
    private val cancelDeliverContractUseCase: CancelDeliverContractUseCase,
) {

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "배송기사 계약 생성", description = "대리점이 소속 배송기사에게 담당 지역과 단가 조건을 제안합니다.")
    @PostMapping
    fun create(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: DeliverContractRequest.Create,
    ): ApiResponse<DeliverContractResponse.Detail> {
        val result = createDeliverContractUseCase.create(request.toCommand(user.userId))

        return ApiResponse.success(
            response = DeliverContractResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "대리점 내 배송기사 계약 목록 조회", description = "로그인한 대리점이 생성한 배송기사 계약 목록을 조회합니다.")
    @GetMapping("/agency/me")
    fun getMyAgencyDeliverContracts(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DeliverContractResponse.List> {
        val results = getMyAgencyDeliverContractsUseCase.getMyAgencyDeliverContracts(user.userId, pageable)

        return ApiResponse.success(
            response = DeliverContractResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "내 배송기사 계약 목록 조회", description = "로그인한 배송기사가 자신에게 요청된 계약 목록을 조회합니다.")
    @GetMapping("/driver/me")
    fun getMyDriverDeliverContracts(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DeliverContractResponse.List> {
        val results = getMyDriverDeliverContractsUseCase.getMyDriverDeliverContracts(user.userId, pageable)

        return ApiResponse.success(
            response = DeliverContractResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "배송기사 계약 수정", description = "대리점이 요청 상태의 배송기사 계약 조건을 수정합니다.")
    @PutMapping("/{deliverContractId}")
    fun update(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable deliverContractId: UUID,
        @RequestBody request: DeliverContractRequest.Update,
    ): ApiResponse<DeliverContractResponse.Detail> {
        val result = updateDeliverContractUseCase.update(request.toCommand(user.userId, deliverContractId))

        return ApiResponse.success(
            response = DeliverContractResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "배송기사 계약 수락", description = "배송기사가 자신에게 요청된 배송기사 계약을 수락합니다.")
    @PostMapping("/{deliverContractId}/accept")
    fun accept(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable deliverContractId: UUID,
    ): ApiResponse<DeliverContractResponse.Detail> {
        val result = acceptDeliverContractUseCase.accept(
            DeliverContractIdCommand(
                userId = user.userId,
                deliverContractId = deliverContractId,
            )
        )

        return ApiResponse.success(
            response = DeliverContractResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "배송기사 계약 거절", description = "배송기사가 자신에게 요청된 배송기사 계약을 거절합니다.")
    @PostMapping("/{deliverContractId}/reject")
    fun reject(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable deliverContractId: UUID,
    ): ApiResponse<DeliverContractResponse.Detail> {
        val result = rejectDeliverContractUseCase.reject(
            DeliverContractIdCommand(
                userId = user.userId,
                deliverContractId = deliverContractId,
            )
        )

        return ApiResponse.success(
            response = DeliverContractResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "배송기사 계약 취소", description = "대리점이 요청 상태의 배송기사 계약을 취소합니다.")
    @PostMapping("/{deliverContractId}/cancel")
    fun cancel(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable deliverContractId: UUID,
    ): ApiResponse<DeliverContractResponse.Detail> {
        val result = cancelDeliverContractUseCase.cancel(
            DeliverContractIdCommand(
                userId = user.userId,
                deliverContractId = deliverContractId,
            )
        )

        return ApiResponse.success(
            response = DeliverContractResponse.Detail.from(result),
        )
    }
}
