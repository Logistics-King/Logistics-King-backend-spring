package logisticsking.com.logisticskingbackendspring.app.proposal.dto

import io.swagger.v3.oas.annotations.media.Schema
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
        @field:Schema(description = "제안 픽업 시작 시간", example = "10:00")
        val pickupStartTime: String,
        @field:Schema(description = "제안 픽업 종료 시간", example = "17:00")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 가능 여부", example = "true")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 회수 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "냉장/냉동 가능 여부", example = "false")
        val coldChainAvailable: Boolean,
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
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainAvailable = coldChainAvailable,
                memo = memo,
            )
        }
    }

    @Schema(name = "ProposalUpdateRequest")
    data class Update(
        @field:Schema(description = "건당 제안 단가", example = "1980")
        val unitPrice: BigDecimal,
        @field:Schema(description = "제안 픽업 시작 시간", example = "09:30")
        val pickupStartTime: String,
        @field:Schema(description = "제안 픽업 종료 시간", example = "16:30")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 가능 여부", example = "false")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 회수 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "냉장/냉동 가능 여부", example = "false")
        val coldChainAvailable: Boolean,
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
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                coldChainAvailable = coldChainAvailable,
                memo = memo,
            )
        }
    }
}
