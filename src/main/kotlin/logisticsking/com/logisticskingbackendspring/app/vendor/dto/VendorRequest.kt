package logisticsking.com.logisticskingbackendspring.app.vendor.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorProductCommand
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

@Schema(description = "화주 요청")
sealed interface VendorRequest {
    data class Create(
        @field:Schema(description = "상호명", example = "안산 옷가게")
        val businessName: String,
        @field:Schema(description = "사업자등록번호", example = "123-45-67890")
        val businessRegistrationNumber: String?,
        @field:Schema(description = "대표자명", example = "김사장")
        val representativeName: String,
        @field:Schema(description = "연락처", example = "010-1234-5678")
        val phoneNumber: String,
        @field:Schema(description = "우편번호", example = "15360")
        val postalCode: String?,
        @field:Schema(description = "사업장 주소", example = "경기도 안산시 상록구 일동")
        val address: String,
        @field:Schema(description = "상세 주소", example = "101호")
        val addressDetail: String?,
        @field:Schema(description = "주 발송 지역", example = "경기도 안산시 일동")
        val mainRegion: String,
    ) : VendorRequest {
        fun toCommand(userId: UUID): CreateVendorCommand {
            return CreateVendorCommand(
                userId = userId,
                businessName = businessName,
                businessRegistrationNumber = businessRegistrationNumber,
                representativeName = representativeName,
                phoneNumber = phoneNumber,
                postalCode = postalCode,
                address = address,
                addressDetail = addressDetail,
                mainRegion = mainRegion,
            )
        }
    }

    data class Update(
        @field:Schema(description = "상호명", example = "안산 의류 스토어")
        val businessName: String,
        @field:Schema(description = "사업자등록번호", example = "123-45-67890")
        val businessRegistrationNumber: String?,
        @field:Schema(description = "대표자명", example = "김사장")
        val representativeName: String,
        @field:Schema(description = "연락처", example = "010-1234-5678")
        val phoneNumber: String,
        @field:Schema(description = "우편번호", example = "15360")
        val postalCode: String?,
        @field:Schema(description = "사업장 주소", example = "경기도 안산시 상록구 일동")
        val address: String,
        @field:Schema(description = "상세 주소", example = "102호")
        val addressDetail: String?,
        @field:Schema(description = "주 발송 지역", example = "경기도 안산시 일동")
        val mainRegion: String,
    ) : VendorRequest {
        fun toCommand(userId: UUID): UpdateVendorCommand {
            return UpdateVendorCommand(
                userId = userId,
                businessName = businessName,
                businessRegistrationNumber = businessRegistrationNumber,
                representativeName = representativeName,
                phoneNumber = phoneNumber,
                postalCode = postalCode,
                address = address,
                addressDetail = addressDetail,
                mainRegion = mainRegion,
            )
        }
    }

    data class CreateProduct(
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val category: ProductCategory,
        @field:Schema(description = "배송 품목명", example = "여성 의류")
        val name: String,
        @field:Schema(description = "품목 설명", example = "일반 의류, 파손 위험 낮음")
        val description: String?,
        @field:Schema(description = "평균 상품 가격", example = "25000")
        val averagePrice: BigDecimal?,
        @field:Schema(description = "평균 무게(g)", example = "700")
        val averageWeightGram: Int?,
        @field:Schema(description = "주요 박스 크기", example = "60")
        val boxSize: String?,
        @field:Schema(description = "파손 주의 여부", example = "false")
        val fragile: Boolean,
        @field:Schema(description = "액체 포함 여부", example = "false")
        val liquid: Boolean,
        @field:Schema(description = "신선식품 여부", example = "false")
        val freshFood: Boolean,
        @field:Schema(description = "냉장/냉동 필요 여부", example = "false")
        val requiresColdChain: Boolean,
    ) : VendorRequest {
        fun toCommand(userId: UUID): CreateVendorProductCommand {
            return CreateVendorProductCommand(
                userId = userId,
                category = category,
                name = name,
                description = description,
                averagePrice = averagePrice,
                averageWeightGram = averageWeightGram,
                boxSize = boxSize,
                fragile = fragile,
                liquid = liquid,
                freshFood = freshFood,
                requiresColdChain = requiresColdChain,
            )
        }
    }

    data class UpdateProduct(
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val category: ProductCategory,
        @field:Schema(description = "배송 품목명", example = "여성 의류")
        val name: String,
        @field:Schema(description = "품목 설명", example = "일반 의류, 반품 발생 가능")
        val description: String?,
        @field:Schema(description = "평균 상품 가격", example = "30000")
        val averagePrice: BigDecimal?,
        @field:Schema(description = "평균 무게(g)", example = "800")
        val averageWeightGram: Int?,
        @field:Schema(description = "주요 박스 크기", example = "60")
        val boxSize: String?,
        @field:Schema(description = "파손 주의 여부", example = "false")
        val fragile: Boolean,
        @field:Schema(description = "액체 포함 여부", example = "false")
        val liquid: Boolean,
        @field:Schema(description = "신선식품 여부", example = "false")
        val freshFood: Boolean,
        @field:Schema(description = "냉장/냉동 필요 여부", example = "false")
        val requiresColdChain: Boolean,
    ) : VendorRequest {
        fun toCommand(
            userId: UUID,
            productId: UUID,
        ): UpdateVendorProductCommand {
            return UpdateVendorProductCommand(
                userId = userId,
                productId = productId,
                category = category,
                name = name,
                description = description,
                averagePrice = averagePrice,
                averageWeightGram = averageWeightGram,
                boxSize = boxSize,
                fragile = fragile,
                liquid = liquid,
                freshFood = freshFood,
                requiresColdChain = requiresColdChain,
            )
        }
    }
}
