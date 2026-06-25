package logisticsking.com.logisticskingbackendspring.app.proposal.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.proposal.command.CreateProposalPriceOfferCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.DecideProposalNegotiationCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.ProposalItemCommand
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "제안 협상 요청")
sealed interface ProposalNegotiationRequest {
    @Schema(name = "ProposalPriceOfferRequest")
    data class PriceOffer(
        @field:Schema(description = "조율 제안 단가", example = "1980")
        val unitPrice: BigDecimal,

        @field:Schema(description = "계약 요청 배송 품목별 조율 제안 단가. 비어 있으면 unitPrice를 모든 품목에 적용합니다.")
        val items: List<Item> = emptyList(),

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
                items = items.map(Item::toCommand),
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

    @Schema(name = "ProposalNegotiationItemRequest")
    data class Item(
        @field:Schema(description = "계약 요청 배송 품목 라인 ID", example = "019b1f44-a741-7000-8000-000000000511")
        val contractRequestItemId: UUID,

        @field:Schema(description = "해당 배송 품목 라인의 조율 제안 단가", example = "1980")
        val unitPrice: BigDecimal,
    ) : ProposalNegotiationRequest {
        fun toCommand(): ProposalItemCommand {
            return ProposalItemCommand(
                contractRequestItemId = contractRequestItemId,
                unitPrice = unitPrice,
            )
        }
    }
}
