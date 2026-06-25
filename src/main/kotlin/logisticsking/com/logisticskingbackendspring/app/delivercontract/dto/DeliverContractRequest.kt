package logisticsking.com.logisticskingbackendspring.app.delivercontract.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.CreateDeliverContractCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.UpdateDeliverContractCommand
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Schema(description = "배송기사 계약 요청")
sealed interface DeliverContractRequest {
    @Schema(name = "DeliverContractCreateRequest")
    data class Create(
        @field:Schema(description = "계약 대상 배송기사 ID", example = "019b1f44-a741-7000-8000-000000000301")
        val deliverId: UUID,

        @field:Schema(description = "계약 담당 지역", example = "경기도 안산시 일동")
        val serviceRegion: String,

        @field:Schema(description = "예상 월 배정 물량", example = "800")
        val expectedMonthlyVolume: Int,

        @field:Schema(description = "기사 지급 건당 단가", example = "900")
        val unitPrice: BigDecimal,

        @field:Schema(description = "계약 시작일", example = "2026-06-01")
        val startDate: LocalDate,

        @field:Schema(description = "계약 종료일", example = "2026-12-31")
        val endDate: LocalDate?,

        @field:Schema(description = "계약 메모", example = "일동 의류 물량 오전 집하 담당")
        val memo: String?,
    ) : DeliverContractRequest {
        fun toCommand(userId: UUID): CreateDeliverContractCommand {
            return CreateDeliverContractCommand(
                userId = userId,
                deliverId = deliverId,
                serviceRegion = serviceRegion,
                expectedMonthlyVolume = expectedMonthlyVolume,
                unitPrice = unitPrice,
                startDate = startDate,
                endDate = endDate,
                memo = memo,
            )
        }
    }

    @Schema(name = "DeliverContractUpdateRequest")
    data class Update(
        @field:Schema(description = "계약 담당 지역", example = "경기도 안산시 본오동")
        val serviceRegion: String,

        @field:Schema(description = "예상 월 배정 물량", example = "1000")
        val expectedMonthlyVolume: Int,

        @field:Schema(description = "기사 지급 건당 단가", example = "950")
        val unitPrice: BigDecimal,

        @field:Schema(description = "계약 시작일", example = "2026-06-01")
        val startDate: LocalDate,

        @field:Schema(description = "계약 종료일", example = "2026-12-31")
        val endDate: LocalDate?,

        @field:Schema(description = "계약 메모", example = "본오동 물량 추가 반영")
        val memo: String?,
    ) : DeliverContractRequest {
        fun toCommand(
            userId: UUID,
            deliverContractId: UUID,
        ): UpdateDeliverContractCommand {
            return UpdateDeliverContractCommand(
                userId = userId,
                deliverContractId = deliverContractId,
                serviceRegion = serviceRegion,
                expectedMonthlyVolume = expectedMonthlyVolume,
                unitPrice = unitPrice,
                startDate = startDate,
                endDate = endDate,
                memo = memo,
            )
        }
    }
}
