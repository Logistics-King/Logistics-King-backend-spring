package logisticsking.com.logisticskingbackendspring.app.contract

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.contract.command.CancelContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.ContractRequestDecisionCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetMyContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetReceivedContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.app.contract.dto.ContractRequestRequest
import logisticsking.com.logisticskingbackendspring.app.contract.dto.ContractRequestResponse
import logisticsking.com.logisticskingbackendspring.app.contract.dto.ContractResponse
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.AcceptContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CancelContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CreateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetReceivedContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.RejectContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.UpdateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetContractRequestProposalsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetOpenContractRequestsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalRequest
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalResponse
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetContractRequestProposalsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetOpenContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.SubmitProposalUseCase
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.common.ListViewScope
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
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
import java.math.BigDecimal
import java.util.UUID

@Tag(name = "ContractRequest", description = "계약 요청 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.ADMIN, UserRole.VENDOR, UserRole.AGENCY])
@RestController
@RequestMapping("/api/v1/contract-requests")
class ContractRequestController(
    private val createContractRequestUseCase: CreateContractRequestUseCase,
    private val getMyContractRequestsUseCase: GetMyContractRequestsUseCase,
    private val getReceivedContractRequestsUseCase: GetReceivedContractRequestsUseCase,
    private val getContractRequestUseCase: GetContractRequestUseCase,
    private val updateContractRequestUseCase: UpdateContractRequestUseCase,
    private val cancelContractRequestUseCase: CancelContractRequestUseCase,
    private val acceptContractRequestUseCase: AcceptContractRequestUseCase,
    private val rejectContractRequestUseCase: RejectContractRequestUseCase,
    private val getOpenContractRequestsUseCase: GetOpenContractRequestsUseCase,
    private val submitProposalUseCase: SubmitProposalUseCase,
    private val getContractRequestProposalsUseCase: GetContractRequestProposalsUseCase,
) {

    @Operation(summary = "계약 요청 생성", description = "화주 또는 대리점이 상대방에게 계약 요청을 생성합니다.")
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

    @Operation(summary = "내 계약 요청 목록 조회", description = "로그인한 사용자가 요청자로 생성한 계약 요청 목록을 조회합니다.")
    @GetMapping
    fun getMyContractRequests(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) category: ProductCategory?,
        @RequestParam(required = false) boxSize: BoxSize?,
        @RequestParam(required = false) coldChainType: ColdChainType?,
        @RequestParam(required = false) status: ContractRequestStatus?,
        @RequestParam(required = false) pickupRegion: String?,
        @RequestParam(required = false) saturdayDeliveryRequired: Boolean?,
        @RequestParam(required = false) returnRequired: Boolean?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<ContractRequestResponse.List> {
        val results = getMyContractRequestsUseCase.getMyContractRequests(
            GetMyContractRequestsCommand(
                userId = user.userId,
                productName = name,
                productCategory = category,
                boxSize = boxSize,
                coldChainType = coldChainType,
                status = status,
                pickupRegion = pickupRegion,
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
            ),
            pageable,
        )

        return ApiResponse.success(
            response = ContractRequestResponse.List.from(results),
        )
    }

    @Operation(summary = "받은 계약 요청 목록 조회", description = "로그인한 사용자가 승인자로 지정된 계약 요청 목록을 조회합니다.")
    @GetMapping("/received")
    fun getReceivedContractRequests(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<ContractRequestResponse.List> {
        val results = getReceivedContractRequestsUseCase.getReceivedContractRequests(
            GetReceivedContractRequestsCommand(userId = user.userId),
            pageable,
        )

        return ApiResponse.success(
            response = ContractRequestResponse.List.from(results),
        )
    }

    @Operation(summary = "계약 요청 상세 조회", description = "로그인한 사용자가 참여자인 계약 요청 상세 정보를 조회합니다.")
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

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "제안 가능 계약 요청 목록 조회", description = "대리점이 제안할 수 있는 OPEN 상태 계약 요청 목록을 조회합니다.")
    @GetMapping("/open")
    fun getOpenContractRequests(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false, defaultValue = "ALL") scope: ListViewScope,
        @RequestParam(required = false) pickupRegion: String?,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) category: ProductCategory?,
        @RequestParam(required = false) boxSize: BoxSize?,
        @RequestParam(required = false) coldChainType: ColdChainType?,
        @RequestParam(required = false) saturdayDeliveryRequired: Boolean?,
        @RequestParam(required = false) returnRequired: Boolean?,
        @RequestParam(required = false) minTargetUnitPrice: BigDecimal?,
        @RequestParam(required = false) maxTargetUnitPrice: BigDecimal?,
        @RequestParam(required = false) vendorName: String?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<ContractRequestResponse.List> {
        val results = getOpenContractRequestsUseCase.getOpenContractRequests(
            GetOpenContractRequestsCommand(
                userId = user.userId,
                scope = scope,
                pickupRegion = pickupRegion,
                productName = name,
                productCategory = category,
                boxSize = boxSize,
                coldChainType = coldChainType,
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                minTargetUnitPrice = minTargetUnitPrice,
                maxTargetUnitPrice = maxTargetUnitPrice,
                vendorName = vendorName,
            ),
            pageable,
        )

        return ApiResponse.success(
            response = ContractRequestResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "계약 요청 제안 제출", description = "대리점이 계약 요청에 단가와 서비스 조건을 제안합니다.")
    @PostMapping("/{contractRequestId}/proposals")
    fun submitProposal(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable contractRequestId: UUID,
        @RequestBody request: ProposalRequest.Submit,
    ): ApiResponse<ProposalResponse.Detail> {
        val result = submitProposalUseCase.submit(request.toCommand(user.userId, contractRequestId))

        return ApiResponse.success(
            response = ProposalResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.VENDOR])
    @Operation(summary = "계약 요청 제안 목록 조회", description = "화주가 자신의 계약 요청에 제출된 제안 목록을 조회합니다.")
    @GetMapping("/{contractRequestId}/proposals")
    fun getProposals(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable contractRequestId: UUID,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<ProposalResponse.List> {
        val results = getContractRequestProposalsUseCase.getContractRequestProposals(
            GetContractRequestProposalsCommand(
                userId = user.userId,
                contractRequestId = contractRequestId,
            ),
            pageable,
        )

        return ApiResponse.success(
            response = ProposalResponse.List.from(results),
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

    @Operation(summary = "계약 요청 수락", description = "승인자로 지정된 사용자가 계약 요청을 수락하고 최종 계약을 생성합니다.")
    @PostMapping("/{contractRequestId}/accept")
    fun accept(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable contractRequestId: UUID,
    ): ApiResponse<ContractResponse.Detail> {
        val result = acceptContractRequestUseCase.accept(
            ContractRequestDecisionCommand(
                userId = user.userId,
                contractRequestId = contractRequestId,
            )
        )

        return ApiResponse.success(
            response = ContractResponse.Detail.from(result),
        )
    }

    @Operation(summary = "계약 요청 거절", description = "승인자로 지정된 사용자가 계약 요청을 거절합니다.")
    @PostMapping("/{contractRequestId}/reject")
    fun reject(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable contractRequestId: UUID,
    ): ApiResponse<ContractRequestResponse.Detail> {
        val result = rejectContractRequestUseCase.reject(
            ContractRequestDecisionCommand(
                userId = user.userId,
                contractRequestId = contractRequestId,
            )
        )

        return ApiResponse.success(
            response = ContractRequestResponse.Detail.from(result),
        )
    }
}
