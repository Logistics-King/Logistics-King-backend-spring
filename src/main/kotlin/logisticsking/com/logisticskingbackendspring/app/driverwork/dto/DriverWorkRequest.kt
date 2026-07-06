package logisticsking.com.logisticskingbackendspring.app.driverwork.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.ApplyDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.AssignDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.CreateDriverWorkCommand
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Schema(description = "배송기사 일감 요청")
sealed interface DriverWorkRequest {
    @Schema(name = "DriverWorkCreateRequest")
    data class Create(
        @field:Schema(description = "최종 계약 ID", example = "019b1f44-a741-7000-8000-000000000030")
        val contractId: UUID,

        @field:Schema(description = "기사 일감 제목", example = "유성구 봉명동 집하")
        val title: String,

        @field:Schema(description = "담당 지역", example = "대전 유성구 봉명동")
        val serviceRegion: String,

        @field:Schema(description = "집하 시작일", example = "2026-07-10")
        val pickupStartDate: LocalDate,

        @field:Schema(description = "집하 종료일", example = "2026-07-31")
        val pickupEndDate: LocalDate?,

        @field:Schema(description = "예상 물량", example = "500")
        val expectedVolume: Int,

        @field:Schema(description = "기사 정산 단가", example = "900")
        val unitPrice: BigDecimal,

        @field:Schema(description = "직접 할당할 배송기사 ID. null이면 OPEN 일감으로 생성합니다.", example = "019b1f44-a741-7000-8000-000000000021")
        val assignedDeliverId: UUID?,

        @field:Schema(description = "메모", example = "오후 집하 중심")
        val memo: String?,
    ) : DriverWorkRequest {
        fun toCommand(userId: UUID): CreateDriverWorkCommand {
            return CreateDriverWorkCommand(
                userId = userId,
                contractId = contractId,
                title = title,
                serviceRegion = serviceRegion,
                pickupStartDate = pickupStartDate,
                pickupEndDate = pickupEndDate,
                expectedVolume = expectedVolume,
                unitPrice = unitPrice,
                assignedDeliverId = assignedDeliverId,
                memo = memo,
            )
        }
    }

    @Schema(name = "DriverWorkAssignRequest")
    data class Assign(
        @field:Schema(description = "할당할 배송기사 ID", example = "019b1f44-a741-7000-8000-000000000021")
        val deliverId: UUID,
    ) : DriverWorkRequest {
        fun toCommand(
            userId: UUID,
            driverWorkId: UUID,
        ): AssignDriverWorkCommand {
            return AssignDriverWorkCommand(
                userId = userId,
                driverWorkId = driverWorkId,
                deliverId = deliverId,
            )
        }
    }

    @Schema(name = "DriverWorkApplyRequest")
    data class Apply(
        @field:Schema(description = "신청 메모", example = "해당 지역 오후 집하 가능합니다.")
        val memo: String?,
    ) : DriverWorkRequest {
        fun toCommand(
            userId: UUID,
            driverWorkId: UUID,
        ): ApplyDriverWorkCommand {
            return ApplyDriverWorkCommand(
                userId = userId,
                driverWorkId = driverWorkId,
                memo = memo,
            )
        }
    }
}
