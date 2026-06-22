package logisticsking.com.logisticskingbackendspring.app.proposal

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.contract.command.AcceptProposalCommand
import logisticsking.com.logisticskingbackendspring.app.contract.dto.ContractResponse
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.AcceptProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.app.proposal.command.GetProposalNegotiationsCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.WithdrawProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalNegotiationRequest
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalNegotiationResponse
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalRequest
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalResponse
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.AcceptProposalNegotiationUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.CreateProposalPriceOfferUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetMyProposalsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetProposalNegotiationsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.RejectProposalNegotiationUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.UpdateProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.WithdrawProposalUseCase
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

@Tag(name = "Proposal", description = "제안 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.AGENCY])
@RestController
@RequestMapping("/api/v1/proposals")
class ProposalController(
    private val getMyProposalsUseCase: GetMyProposalsUseCase,
    private val updateProposalUseCase: UpdateProposalUseCase,
    private val withdrawProposalUseCase: WithdrawProposalUseCase,
    private val acceptProposalUseCase: AcceptProposalUseCase,
    private val getProposalNegotiationsUseCase: GetProposalNegotiationsUseCase,
    private val createProposalPriceOfferUseCase: CreateProposalPriceOfferUseCase,
    private val acceptProposalNegotiationUseCase: AcceptProposalNegotiationUseCase,
    private val rejectProposalNegotiationUseCase: RejectProposalNegotiationUseCase,
) {

    @Operation(summary = "내 제안 목록 조회", description = "로그인한 대리점의 제안 목록을 조회합니다.")
    @GetMapping("/me")
    fun getMyProposals(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<ProposalResponse.List> {
        val results = getMyProposalsUseCase.getMyProposals(user.userId, pageable)

        return ApiResponse.success(
            response = ProposalResponse.List.from(results),
        )
    }

    @Operation(summary = "제안 수정", description = "대리점이 자신이 제출한 제안의 단가와 조건을 수정합니다.")
    @PutMapping("/{proposalId}")
    fun update(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable proposalId: UUID,
        @RequestBody request: ProposalRequest.Update,
    ): ApiResponse<ProposalResponse.Detail> {
        val result = updateProposalUseCase.update(request.toCommand(user.userId, proposalId))

        return ApiResponse.success(
            response = ProposalResponse.Detail.from(result),
        )
    }

    @Operation(summary = "제안 철회", description = "대리점이 자신이 제출한 제안을 철회합니다.")
    @PostMapping("/{proposalId}/withdraw")
    fun withdraw(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable proposalId: UUID,
    ): ApiResponse<ProposalResponse.Detail> {
        val result = withdrawProposalUseCase.withdraw(
            WithdrawProposalCommand(
                userId = user.userId,
                proposalId = proposalId,
            )
        )

        return ApiResponse.success(
            response = ProposalResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.VENDOR])
    @Operation(summary = "제안 수락 및 최종 계약 생성", description = "화주가 제안을 수락하고 최종 계약을 생성합니다.")
    @PostMapping("/{proposalId}/accept")
    fun accept(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable proposalId: UUID,
    ): ApiResponse<ContractResponse.Detail> {
        val result = acceptProposalUseCase.accept(
            AcceptProposalCommand(
                userId = user.userId,
                proposalId = proposalId,
            )
        )

        return ApiResponse.success(
            response = ContractResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.VENDOR, UserRole.AGENCY])
    @Operation(summary = "제안 협상 이벤트 목록 조회", description = "화주 또는 대리점이 제안의 가격 조율 이벤트 목록을 조회합니다.")
    @GetMapping("/{proposalId}/negotiations")
    fun getNegotiations(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable proposalId: UUID,
    ): ApiResponse<ProposalNegotiationResponse.List> {
        val results = getProposalNegotiationsUseCase.getNegotiations(
            GetProposalNegotiationsCommand(
                userId = user.userId,
                proposalId = proposalId,
            )
        )

        return ApiResponse.success(
            response = ProposalNegotiationResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.VENDOR, UserRole.AGENCY])
    @Operation(summary = "제안 가격 조율 등록", description = "화주 또는 대리점이 제안 단가 조율 이벤트를 등록합니다.")
    @PostMapping("/{proposalId}/negotiations/price-offers")
    fun createPriceOffer(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable proposalId: UUID,
        @RequestBody request: ProposalNegotiationRequest.PriceOffer,
    ): ApiResponse<ProposalNegotiationResponse.Detail> {
        val result = createProposalPriceOfferUseCase.createPriceOffer(request.toCommand(user.userId, proposalId))

        return ApiResponse.success(
            response = ProposalNegotiationResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.VENDOR, UserRole.AGENCY])
    @Operation(summary = "제안 가격 조율 수락", description = "상대방이 제안한 가격 조율 이벤트를 수락합니다.")
    @PostMapping("/{proposalId}/negotiations/{eventId}/accept")
    fun acceptNegotiation(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable proposalId: UUID,
        @PathVariable eventId: UUID,
        @RequestBody request: ProposalNegotiationRequest.Decision,
    ): ApiResponse<ProposalNegotiationResponse.Detail> {
        val result = acceptProposalNegotiationUseCase.acceptNegotiation(
            request.toCommand(
                userId = user.userId,
                proposalId = proposalId,
                eventId = eventId,
            )
        )

        return ApiResponse.success(
            response = ProposalNegotiationResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.VENDOR, UserRole.AGENCY])
    @Operation(summary = "제안 가격 조율 거절", description = "상대방이 제안한 가격 조율 이벤트를 거절합니다.")
    @PostMapping("/{proposalId}/negotiations/{eventId}/reject")
    fun rejectNegotiation(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable proposalId: UUID,
        @PathVariable eventId: UUID,
        @RequestBody request: ProposalNegotiationRequest.Decision,
    ): ApiResponse<ProposalNegotiationResponse.Detail> {
        val result = rejectProposalNegotiationUseCase.rejectNegotiation(
            request.toCommand(
                userId = user.userId,
                proposalId = proposalId,
                eventId = eventId,
            )
        )

        return ApiResponse.success(
            response = ProposalNegotiationResponse.Detail.from(result),
        )
    }
}
