package logisticsking.com.logisticskingbackendspring.app.contract.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import java.math.BigDecimal

@Schema(description = "계약 요청 응답")
sealed interface ContractRequestResponse {
    @Schema(name = "ContractRequestDetailResponse")
    data class Detail(
        @field:Schema(description = "계약 요청 ID", example = "019b1f44-a741-7000-8000-000000000011")
        val contractRequestId: String,
        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val vendorId: String,
        @field:Schema(description = "화주 배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: String?,
        @field:Schema(description = "픽업 지역", example = "경기도 안산시 일동")
        val pickupRegion: String,
        @field:Schema(description = "픽업 상세 주소", example = "경기도 안산시 상록구 일동 101호")
        val pickupAddress: String?,
        @field:Schema(description = "월 예상 물량", example = "800")
        val monthlyVolume: Int,
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val productCategory: String,
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
        @field:Schema(description = "냉장/냉동 필요 여부", example = "false")
        val coldChainRequired: Boolean,
        @field:Schema(description = "희망 단가", example = "2000")
        val targetUnitPrice: BigDecimal?,
        @field:Schema(description = "요청 메모", example = "의류 중심이며 평일 오전 픽업을 선호합니다.")
        val memo: String?,
        @field:Schema(description = "계약 요청 상태", example = "OPEN")
        val status: String,
    ) : ContractRequestResponse {
        companion object {
            fun from(result: ContractRequestResult): Detail {
                return Detail(
                    contractRequestId = result.contractRequestId.toString(),
                    vendorId = result.vendorId.toString(),
                    productId = result.productId?.toString(),
                    pickupRegion = result.pickupRegion,
                    pickupAddress = result.pickupAddress,
                    monthlyVolume = result.monthlyVolume,
                    productCategory = result.productCategory.name,
                    productName = result.productName,
                    boxSize = result.boxSize,
                    pickupStartTime = result.pickupStartTime,
                    pickupEndTime = result.pickupEndTime,
                    saturdayDeliveryRequired = result.saturdayDeliveryRequired,
                    returnRequired = result.returnRequired,
                    coldChainRequired = result.coldChainRequired,
                    targetUnitPrice = result.targetUnitPrice,
                    memo = result.memo,
                    status = result.status.name,
                )
            }
        }
    }

    @Schema(name = "ContractRequestListResponse")
    data class List(
        @field:Schema(description = "계약 요청 목록")
        val contractRequests: kotlin.collections.List<Detail>,
    ) : ContractRequestResponse
}
