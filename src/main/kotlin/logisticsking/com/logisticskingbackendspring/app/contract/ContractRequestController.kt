package logisticsking.com.logisticskingbackendspring.app.contract

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.contract.command.CancelContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.dto.ContractRequestRequest
import logisticsking.com.logisticskingbackendspring.app.contract.dto.ContractRequestResponse
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CancelContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CreateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.UpdateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "ContractRequest", description = "계약 요청 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.VENDOR])
@RestController
@RequestMapping("/api/v1/contract-requests")
class ContractRequestController(
    private val createContractRequestUseCase: CreateContractRequestUseCase,
    private val getMyContractRequestsUseCase: GetMyContractRequestsUseCase,
    private val getContractRequestUseCase: GetContractRequestUseCase,
    private val updateContractRequestUseCase: UpdateContractRequestUseCase,
    private val cancelContractRequestUseCase: CancelContractRequestUseCase,
) {

    @Operation(summary = "계약 요청 생성", description = "로그인한 화주가 대리점 제안을 받기 위한 계약 요청을 생성합니다.")
    @PostMapping
    fun create(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: ContractRequestRequest.Create,
    ): ApiResponse<ContractRequestResponse.Detail> {
        val result = createContractRequestUseCase.create(request.toCommand(user.userId))

        return ApiResponse.success(
            response = ContractRequestResponse.Detail.from(result),
        )
    }

    @Operation(summary = "내 계약 요청 목록 조회", description = "로그인한 화주의 계약 요청 목록을 조회합니다.")
    @GetMapping
    fun getMyContractRequests(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<ContractRequestResponse.List> {
        val results = getMyContractRequestsUseCase.getMyContractRequests(user.userId)

        return ApiResponse.success(
            response = ContractRequestResponse.List(
                contractRequests = results.map(ContractRequestResponse.Detail::from),
            )
        )
    }

    @Operation(summary = "계약 요청 상세 조회", description = "로그인한 화주의 계약 요청 상세 정보를 조회합니다.")
    @GetMapping("/{contractRequestId}")
    fun get(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable contractRequestId: UUID,
    ): ApiResponse<ContractRequestResponse.Detail> {
        val result = getContractRequestUseCase.get(
            GetContractRequestCommand(
                userId = user.userId,
                contractRequestId = contractRequestId,
            )
        )

        return ApiResponse.success(
            response = ContractRequestResponse.Detail.from(result),
        )
    }

    @Operation(summary = "계약 요청 수정", description = "로그인한 화주의 계약 요청 정보를 수정합니다.")
    @PutMapping("/{contractRequestId}")
    fun update(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable contractRequestId: UUID,
        @RequestBody request: ContractRequestRequest.Update,
    ): ApiResponse<ContractRequestResponse.Detail> {
        val result = updateContractRequestUseCase.update(request.toCommand(user.userId, contractRequestId))

        return ApiResponse.success(
            response = ContractRequestResponse.Detail.from(result),
        )
    }

    @Operation(summary = "계약 요청 취소", description = "로그인한 화주의 계약 요청을 취소합니다.")
    @PostMapping("/{contractRequestId}/cancel")
    fun cancel(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable contractRequestId: UUID,
    ): ApiResponse<ContractRequestResponse.Detail> {
        val result = cancelContractRequestUseCase.cancel(
            CancelContractRequestCommand(
                userId = user.userId,
                contractRequestId = contractRequestId,
            )
        )

        return ApiResponse.success(
            response = ContractRequestResponse.Detail.from(result),
        )
    }
}
