package logisticsking.com.logisticskingbackendspring.app.proposal.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalNegotiationEventResult
import java.math.BigDecimal

@Schema(description = "제안 협상 응답")
sealed interface ProposalNegotiationResponse {
    @Schema(name = "ProposalNegotiationEventResponse")
    data class Detail(
        @field:Schema(description = "협상 이벤트 ID", example = "019b1f44-a741-7000-8000-000000000301")
        val eventId: String,
        @field:Schema(description = "제안 ID", example = "019b1f44-a741-7000-8000-000000000101")
        val proposalId: String,
        @field:Schema(description = "제안 내 이벤트 순번", example = "1")
        val sequence: Long,
        @field:Schema(description = "이벤트 작성 주체 (VENDOR, AGENCY)", example = "VENDOR")
        val actorType: String,
        @field:Schema(description = "이벤트 타입 (PRICE_OFFER, PRICE_ACCEPTED, PRICE_REJECTED)", example = "PRICE_OFFER")
        val eventType: String,
        @field:Schema(description = "조율 제안 단가. PRICE_OFFER일 때 내려갑니다.", example = "1980")
        val unitPrice: BigDecimal?,
        @field:Schema(description = "협상 메모", example = "해당 단가로 진행 가능할까요?")
        val memo: String?,
        @field:Schema(description = "이벤트 상태 (PENDING, ACCEPTED, REJECTED, RECORDED)", example = "PENDING")
        val status: String,
    ) : ProposalNegotiationResponse {
        companion object {
            fun from(result: ProposalNegotiationEventResult): Detail {
                return Detail(
                    eventId = result.eventId.toString(),
                    proposalId = result.proposalId.toString(),
                    sequence = result.sequence,
                    actorType = result.actorType.name,
                    eventType = result.eventType.name,
                    unitPrice = result.unitPrice,
                    memo = result.memo,
                    status = result.status.name,
                )
            }
        }
    }

    @Schema(name = "ProposalNegotiationListResponse")
    data class List(
        @field:Schema(description = "협상 이벤트 목록")
        val items: kotlin.collections.List<Detail>,
    ) : ProposalNegotiationResponse {
        companion object {
            fun from(results: kotlin.collections.List<ProposalNegotiationEventResult>): List {
                return List(items = results.map(Detail::from))
            }
        }
    }
}
