package logisticsking.com.logisticskingbackendspring.app.proposal.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.proposal.result.ProposalNegotiationEventItemResult
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

        @field:Schema(description = "계약 요청 배송 품목별 조율 제안 단가")
        val items: kotlin.collections.List<Item>,

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
                    items = result.items.map(Item::from),
                    memo = result.memo,
                    status = result.status.name,
                )
            }
        }
    }

    @Schema(name = "ProposalNegotiationItemResponse")
    data class Item(
        @field:Schema(description = "협상 이벤트 품목 단가 라인 ID", example = "019b1f44-a741-7000-8000-000000000711")
        val itemId: String,

        @field:Schema(description = "계약 요청 배송 품목 라인 ID", example = "019b1f44-a741-7000-8000-000000000511")
        val contractRequestItemId: String,

        @field:Schema(description = "해당 배송 품목 라인의 조율 제안 단가", example = "1980")
        val unitPrice: BigDecimal,
    ) : ProposalNegotiationResponse {
        companion object {
            fun from(result: ProposalNegotiationEventItemResult): Item {
                return Item(
                    itemId = result.itemId.toString(),
                    contractRequestItemId = result.contractRequestItemId.toString(),
                    unitPrice = result.unitPrice,
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
