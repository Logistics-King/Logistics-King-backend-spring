package logisticsking.com.logisticskingbackendspring.app.vendor.dto

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.app.vendor.result.ProductResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import org.springframework.data.domain.Page
import java.math.BigDecimal

@Schema(description = "화주 응답")
sealed interface VendorResponse {
    @Schema(name = "VendorDetailResponse")
    data class Detail(
        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val vendorId: String,
        @field:Schema(description = "사용자 ID", example = "019b1f44-a741-7000-8000-000000000002")
        val userId: String,
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
    ) : VendorResponse {
        companion object {
            fun from(result: VendorResult): Detail {
                return Detail(
                    vendorId = result.vendorId.toString(),
                    userId = result.userId.toString(),
                    businessName = result.businessName,
                    businessRegistrationNumber = result.businessRegistrationNumber,
                    representativeName = result.representativeName,
                    phoneNumber = result.phoneNumber,
                    postalCode = result.postalCode,
                    address = result.address,
                    addressDetail = result.addressDetail,
                    mainRegion = result.mainRegion,
                )
            }
        }
    }

    @Schema(name = "VendorSummaryResponse")
    data class Summary(
        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val vendorId: String,
        @field:Schema(description = "사용자 ID", example = "019b1f44-a741-7000-8000-000000000002")
        val userId: String,
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
    ) : VendorResponse {
        companion object {
            fun from(result: VendorResult): Summary {
                return Summary(
                    vendorId = result.vendorId.toString(),
                    userId = result.userId.toString(),
                    businessName = result.businessName,
                    businessRegistrationNumber = result.businessRegistrationNumber,
                    representativeName = result.representativeName,
                    phoneNumber = result.phoneNumber,
                    postalCode = result.postalCode,
                    address = result.address,
                    addressDetail = result.addressDetail,
                    mainRegion = result.mainRegion,
                )
            }
        }
    }

    @Schema(name = "ProductDetailResponse")
    data class ProductDetail(
        @field:Schema(description = "배송 품목 ID", example = "019b1f44-a741-7000-8000-000000000003")
        val productId: String,
        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val vendorId: String,
        @field:Schema(description = "화주 기본 정보. 대리점 일감 조회에서는 포함되고, 내 배송 품목 조회에서는 null입니다.")
        val vendor: Summary?,
        @field:Schema(description = "품목 카테고리", example = "CLOTHING")
        val category: String,
        @field:Schema(description = "배송 품목명", example = "여성 의류")
        val name: String,
        @field:Schema(description = "품목 설명", example = "일반 의류")
        val description: String?,
        @field:Schema(description = "평균 상품 가격", example = "25000")
        val averagePrice: BigDecimal?,
        @field:Schema(description = "평균 무게(g)", example = "700")
        val averageWeightGram: Int?,
        @field:Schema(description = "주요 박스 크기", example = "SIZE_60")
        val boxSize: BoxSize?,
        @field:Schema(description = "박스 수량", example = "800")
        val boxQuantity: Int,
        @field:Schema(description = "낱개 수량", example = "0")
        val itemQuantity: Int,
        @field:Schema(description = "배송 목적지 우편번호", example = "06164")
        val destinationPostalCode: String?,
        @field:Schema(description = "배송 목적지 주소", example = "서울특별시 강남구 테헤란로 521")
        val destinationAddress: String,
        @field:Schema(description = "배송 목적지 상세 주소", example = "10층")
        val destinationAddressDetail: String?,
        @field:Schema(description = "파손 주의 여부", example = "false")
        val fragile: Boolean,
        @field:Schema(description = "액체 포함 여부", example = "false")
        val liquid: Boolean,
        @field:Schema(description = "신선식품 여부", example = "false")
        val freshFood: Boolean,
        @field:Schema(description = "콜드체인 필요 타입 (NONE, REFRIGERATED, FROZEN)", example = "NONE")
        val coldChainType: ColdChainType,
    ) : VendorResponse {
        companion object {
            fun from(result: ProductResult): ProductDetail {
                return ProductDetail(
                    productId = result.productId.toString(),
                    vendorId = result.vendorId.toString(),
                    vendor = result.vendor?.let(Summary::from),
                    category = result.category.name,
                    name = result.name,
                    description = result.description,
                    averagePrice = result.averagePrice,
                    averageWeightGram = result.averageWeightGram,
                    boxSize = result.boxSize,
                    boxQuantity = result.boxQuantity,
                    itemQuantity = result.itemQuantity,
                    destinationPostalCode = result.destinationPostalCode,
                    destinationAddress = result.destinationAddress,
                    destinationAddressDetail = result.destinationAddressDetail,
                    fragile = result.fragile,
                    liquid = result.liquid,
                    freshFood = result.freshFood,
                    coldChainType = result.coldChainType,
                )
            }
        }
    }

    @Schema(name = "ProductListResponse")
    data class ProductList(
        @field:Schema(description = "배송 품목 목록")
        val items: kotlin.collections.List<ProductDetail>,
        @field:Schema(description = "현재 페이지 번호. 0부터 시작합니다.", example = "0")
        val page: Int,
        @field:Schema(description = "페이지 크기", example = "20")
        val size: Int,
        @field:Schema(description = "전체 데이터 수", example = "128")
        val totalElements: Long,
        @field:Schema(description = "전체 페이지 수", example = "7")
        val totalPages: Int,
        @field:Schema(description = "다음 페이지 존재 여부", example = "true")
        val hasNext: Boolean,
        @field:Schema(description = "이전 페이지 존재 여부", example = "false")
        val hasPrevious: Boolean,
    ) : VendorResponse {
        companion object {
            fun from(results: Page<ProductResult>): ProductList {
                val page = PageResponse.from(results, ProductDetail::from)

                return ProductList(
                    items = page.items,
                    page = page.page,
                    size = page.size,
                    totalElements = page.totalElements,
                    totalPages = page.totalPages,
                    hasNext = page.hasNext,
                    hasPrevious = page.hasPrevious,
                )
            }
        }
    }
}
