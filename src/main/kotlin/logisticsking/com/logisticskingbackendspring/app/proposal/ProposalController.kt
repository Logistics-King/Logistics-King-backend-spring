package logisticsking.com.logisticskingbackendspring.app.proposal

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.app.proposal.command.WithdrawProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalRequest
import logisticsking.com.logisticskingbackendspring.app.proposal.dto.ProposalResponse
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.GetMyProposalsUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.UpdateProposalUseCase
import logisticsking.com.logisticskingbackendspring.app.proposal.usecase.WithdrawProposalUseCase
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

@Tag(name = "Proposal", description = "제안 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.AGENCY])
@RestController
@RequestMapping("/api/v1/proposals")
class ProposalController(
    private val getMyProposalsUseCase: GetMyProposalsUseCase,
    private val updateProposalUseCase: UpdateProposalUseCase,
    private val withdrawProposalUseCase: WithdrawProposalUseCase,
) {

    @Operation(summary = "내 제안 목록 조회", description = "로그인한 대리점의 제안 목록을 조회합니다.")
    @GetMapping("/me")
    fun getMyProposals(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<ProposalResponse.List> {
        val results = getMyProposalsUseCase.getMyProposals(user.userId)

        return ApiResponse.success(
            response = ProposalResponse.List(
                proposals = results.map(ProposalResponse.Detail::from),
            )
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
}
