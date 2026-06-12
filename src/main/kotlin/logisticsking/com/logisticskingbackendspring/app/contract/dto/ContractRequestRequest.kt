package logisticsking.com.logisticskingbackendspring.app.contract.dto

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "계약 요청")
sealed interface ContractRequestRequest {
    @Schema(name = "ContractRequestCreateRequest")
    data class Create(
        @field:Schema(description = "화주 배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: UUID?,
        @field:Schema(description = "픽업 지역", example = "경기도 안산시 일동")
        val pickupRegion: String,
        @field:Schema(description = "픽업 상세 주소", example = "경기도 안산시 상록구 일동 101호")
        val pickupAddress: String?,
        @field:Schema(description = "월 예상 물량", example = "800")
        val monthlyVolume: Int,
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: ProductCategory,
        @field:Schema(description = "품목명", example = "일반 의류")
        val productName: String,
        @field:Schema(description = "주요 박스 크기", example = "60")
        val boxSize: String,
        @field:Schema(description = "픽업 희망 시작 시간", example = "09:00")
        val pickupStartTime: String,
        @field:Schema(description = "픽업 희망 종료 시간", example = "18:00")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 필요 여부", example = "true")
        val saturdayDeliveryRequired: Boolean,
        @field:Schema(description = "반품 처리 필요 여부", example = "true")
        val returnRequired: Boolean,
        @field:Schema(description = "콜드체인 필요 타입 (NONE, REFRIGERATED, FROZEN)", example = "NONE")
        val coldChainType: ColdChainType,
        @field:Schema(description = "희망 단가", example = "2000")
        val targetUnitPrice: BigDecimal?,
        @field:Schema(description = "요청 메모", example = "의류 중심이며 평일 오전 픽업을 선호합니다.")
        val memo: String?,
    ) : ContractRequestRequest {
        fun toCommand(userId: UUID): CreateContractRequestCommand {
            return CreateContractRequestCommand(
                userId = userId,
                productId = productId,
                pickupRegion = pickupRegion,
                pickupAddress = pickupAddress,
                monthlyVolume = monthlyVolume,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
                memo = memo,
            )
        }
    }

    @Schema(name = "ContractRequestUpdateRequest")
    data class Update(
        @field:Schema(description = "화주 배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: UUID?,
        @field:Schema(description = "픽업 지역", example = "경기도 안산시 본오동")
        val pickupRegion: String,
        @field:Schema(description = "픽업 상세 주소", example = "경기도 안산시 상록구 본오동 202호")
        val pickupAddress: String?,
        @field:Schema(description = "월 예상 물량", example = "1000")
        val monthlyVolume: Int,
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: ProductCategory,
        @field:Schema(description = "품목명", example = "여성 의류")
        val productName: String,
        @field:Schema(description = "주요 박스 크기", example = "80")
        val boxSize: String,
        @field:Schema(description = "픽업 희망 시작 시간", example = "10:00")
        val pickupStartTime: String,
        @field:Schema(description = "픽업 희망 종료 시간", example = "17:00")
        val pickupEndTime: String,
        @field:Schema(description = "토요일 배송 필요 여부", example = "false")
        val saturdayDeliveryRequired: Boolean,
        @field:Schema(description = "반품 처리 필요 여부", example = "true")
        val returnRequired: Boolean,
        @field:Schema(description = "콜드체인 필요 타입 (NONE, REFRIGERATED, FROZEN)", example = "NONE")
        val coldChainType: ColdChainType,
        @field:Schema(description = "희망 단가", example = "2100")
        val targetUnitPrice: BigDecimal?,
        @field:Schema(description = "요청 메모", example = "반품 회수가 자주 발생합니다.")
        val memo: String?,
    ) : ContractRequestRequest {
        fun toCommand(
            userId: UUID,
            contractRequestId: UUID,
        ): UpdateContractRequestCommand {
            return UpdateContractRequestCommand(
                userId = userId,
                contractRequestId = contractRequestId,
                productId = productId,
                pickupRegion = pickupRegion,
                pickupAddress = pickupAddress,
                monthlyVolume = monthlyVolume,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
                memo = memo,
            )
        }
    }
}
