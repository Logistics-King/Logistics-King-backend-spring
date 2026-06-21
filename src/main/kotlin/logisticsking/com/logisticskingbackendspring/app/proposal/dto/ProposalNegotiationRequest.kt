package logisticsking.com.logisticskingbackendspring.app.proposal.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.proposal.command.CreateProposalPriceOfferCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.DecideProposalNegotiationCommand
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "제안 협상 요청")
sealed interface ProposalNegotiationRequest {
    @Schema(name = "ProposalPriceOfferRequest")
    data class PriceOffer(
        @field:Schema(description = "조율 제안 단가", example = "1980")
        val unitPrice: BigDecimal,
        @field:Schema(description = "협상 메모", example = "월 800박스 기준이면 1980원까지 가능합니다.")
        val memo: String?,
    ) : ProposalNegotiationRequest {
        fun toCommand(
            userId: UUID,
            proposalId: UUID,
        ): CreateProposalPriceOfferCommand {
            return CreateProposalPriceOfferCommand(
                userId = userId,
                proposalId = proposalId,
                unitPrice = unitPrice,
                memo = memo,
            )
        }
    }

    @Schema(name = "ProposalNegotiationDecisionRequest")
    data class Decision(
        @field:Schema(description = "응답 메모", example = "해당 단가로 진행하겠습니다.")
        val memo: String?,
    ) : ProposalNegotiationRequest {
        fun toCommand(
            userId: UUID,
            proposalId: UUID,
            eventId: UUID,
        ): DecideProposalNegotiationCommand {
            return DecideProposalNegotiationCommand(
                userId = userId,
                proposalId = proposalId,
                eventId = eventId,
                memo = memo,
            )
        }
    }
}
