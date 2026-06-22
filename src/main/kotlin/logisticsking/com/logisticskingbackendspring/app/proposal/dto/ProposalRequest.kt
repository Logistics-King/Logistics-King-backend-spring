package logisticsking.com.logisticskingbackendspring.app.proposal.dto

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.proposal.command.ProposalItemCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.SubmitProposalCommand
import logisticsking.com.logisticskingbackendspring.app.proposal.command.UpdateProposalCommand
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "제안 요청")
sealed interface ProposalRequest {
    @Schema(name = "ProposalSubmitRequest")
    data class Submit(
        @field:Schema(description = "건당 제안 단가", example = "2050")
        val unitPrice: BigDecimal,
        @field:Schema(description = "계약 요청 배송 품목별 제안 단가. 비어 있으면 unitPrice를 모든 품목에 적용합니다.")
        val items: List<Item> = emptyList(),
        @field:Schema(description = "제안 픽업 시작 시간", example = "10:00")
        val pickupStartTime: String,
        @field:Schema(description = "제안 픽업 종료 시간", example = "17:00")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 가능 여부", example = "true")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 회수 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "지원 콜드체인 타입 (NONE, REFRIGERATED, FROZEN)", example = "REFRIGERATED")
        val coldChainType: ColdChainType,
        @field:Schema(description = "제안 메모", example = "의류 800박스 기준 집하 가능합니다.")
        val memo: String?,
    ) : ProposalRequest {
        fun toCommand(
            userId: UUID,
            contractRequestId: UUID,
        ): SubmitProposalCommand {
            return SubmitProposalCommand(
                userId = userId,
                contractRequestId = contractRequestId,
                unitPrice = unitPrice,
                items = items.map(Item::toCommand),
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainType = coldChainType,
                memo = memo,
            )
        }
    }

    @Schema(name = "ProposalUpdateRequest")
    data class Update(
        @field:Schema(description = "건당 제안 단가", example = "1980")
        val unitPrice: BigDecimal,
        @field:Schema(description = "계약 요청 배송 품목별 제안 단가. 비어 있으면 unitPrice를 모든 품목에 적용합니다.")
        val items: List<Item> = emptyList(),
        @field:Schema(description = "제안 픽업 시작 시간", example = "09:30")
        val pickupStartTime: String,
        @field:Schema(description = "제안 픽업 종료 시간", example = "16:30")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 가능 여부", example = "false")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 회수 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "지원 콜드체인 타입 (NONE, REFRIGERATED, FROZEN)", example = "REFRIGERATED")
        val coldChainType: ColdChainType,
        @field:Schema(description = "제안 메모", example = "오전 집하 기준 단가 조정 가능합니다.")
        val memo: String?,
    ) : ProposalRequest {
        fun toCommand(
            userId: UUID,
            proposalId: UUID,
        ): UpdateProposalCommand {
            return UpdateProposalCommand(
                userId = userId,
                proposalId = proposalId,
                unitPrice = unitPrice,
                items = items.map(Item::toCommand),
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainType = coldChainType,
                memo = memo,
            )
        }
    }

    @Schema(name = "ProposalItemRequest")
    data class Item(
        @field:Schema(description = "계약 요청 배송 품목 라인 ID", example = "019b1f44-a741-7000-8000-000000000511")
        val contractRequestItemId: UUID,
        @field:Schema(description = "해당 배송 품목 라인의 제안 단가", example = "2050")
        val unitPrice: BigDecimal,
    ) : ProposalRequest {
        fun toCommand(): ProposalItemCommand {
            return ProposalItemCommand(
                contractRequestItemId = contractRequestItemId,
                unitPrice = unitPrice,
            )
        }
    }
}
