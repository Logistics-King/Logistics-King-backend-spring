package logisticsking.com.logisticskingbackendspring.app.deliver.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.deliver.command.CreateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.command.UpdateDeliverCommand
import java.util.UUID

@Schema(description = "배송기사 요청")
sealed interface DeliverRequest {
    @Schema(name = "DeliverCreateRequest")
    data class Create(
        @field:Schema(description = "소속 대리점 ID", example = "019b1f44-a741-7000-8000-000000000010")
        val agencyId: UUID,

        @field:Schema(description = "기사명", example = "김택배")
        val driverName: String,

        @field:Schema(description = "연락처", example = "010-1234-5678")
        val phoneNumber: String,

        @field:Schema(description = "차량번호", example = "12가3456")
        val vehicleNumber: String?,

        @field:Schema(description = "담당 가능 지역", example = "[\"경기도 안산시 일동\", \"경기도 안산시 본오동\"]")
        val serviceRegions: List<String>,

        @field:Schema(description = "운영 활성 여부", example = "true")
        val active: Boolean,

        @field:Schema(description = "메모", example = "오전 집하 담당")
        val memo: String?,
    ) : DeliverRequest {
        fun toCommand(userId: UUID): CreateDeliverCommand {
            return CreateDeliverCommand(
                userId = userId,
                agencyId = agencyId,
                driverName = driverName,
                phoneNumber = phoneNumber,
                vehicleNumber = vehicleNumber,
                serviceRegions = serviceRegions,
                active = active,
                memo = memo,
            )
        }
    }

    @Schema(name = "DeliverUpdateRequest")
    data class Update(
        @field:Schema(description = "소속 대리점 ID", example = "019b1f44-a741-7000-8000-000000000010")
        val agencyId: UUID,

        @field:Schema(description = "기사명", example = "박배송")
        val driverName: String,

        @field:Schema(description = "연락처", example = "010-9876-5432")
        val phoneNumber: String,

        @field:Schema(description = "차량번호", example = "34나5678")
        val vehicleNumber: String?,

        @field:Schema(description = "담당 가능 지역", example = "[\"경기도 안산시 사동\"]")
        val serviceRegions: List<String>,

        @field:Schema(description = "운영 활성 여부", example = "false")
        val active: Boolean,

        @field:Schema(description = "메모", example = "오후 집하 담당")
        val memo: String?,
    ) : DeliverRequest {
        fun toCommand(userId: UUID): UpdateDeliverCommand {
            return UpdateDeliverCommand(
                userId = userId,
                agencyId = agencyId,
                driverName = driverName,
                phoneNumber = phoneNumber,
                vehicleNumber = vehicleNumber,
                serviceRegions = serviceRegions,
                active = active,
                memo = memo,
            )
        }
    }
}
