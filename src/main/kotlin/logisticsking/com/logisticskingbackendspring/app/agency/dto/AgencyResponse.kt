package logisticsking.com.logisticskingbackendspring.app.agency.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.app.common.PageResponse
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import org.springframework.data.domain.Page

@Schema(description = "대리점 응답")
sealed interface AgencyResponse {
    @Schema(name = "AgencyDetailResponse")
    data class Detail(
        @field:Schema(description = "대리점 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val agencyId: String,
        @field:Schema(description = "사용자 ID", example = "019b1f44-a741-7000-8000-000000000002")
        val userId: String,
        @field:Schema(description = "택배사", example = "CJ")
        val carrier: String,
        @field:Schema(description = "대리점명", example = "CJ 일동대리점")
        val agencyName: String,
        @field:Schema(description = "사업자등록번호", example = "123-45-67890")
        val businessRegistrationNumber: String?,
        @field:Schema(description = "대표자명", example = "김대표")
        val representativeName: String,
        @field:Schema(description = "연락처", example = "010-1234-5678")
        val phoneNumber: String,
        @field:Schema(description = "우편번호", example = "15360")
        val postalCode: String?,
        @field:Schema(description = "대리점 주소", example = "경기도 안산시 상록구 일동")
        val address: String,
        @field:Schema(description = "상세 주소", example = "1층")
        val addressDetail: String?,
        @field:Schema(description = "주 담당 지역", example = "경기도 안산시 일동")
        val mainRegion: String,
        @field:Schema(description = "담당 가능 지역")
        val serviceRegions: kotlin.collections.List<String>,
        @field:Schema(description = "평일 픽업 시작 시간", example = "09:00")
        val weekdayPickupStartTime: String?,
        @field:Schema(description = "평일 픽업 종료 시간", example = "18:00")
        val weekdayPickupEndTime: String?,
        @field:Schema(description = "토요일 집하 가능 여부", example = "true")
        val saturdayPickupAvailable: Boolean,
        @field:Schema(description = "토요일 배송 가능 여부", example = "true")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 처리 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "지원 콜드체인 타입 목록 (NONE, REFRIGERATED, FROZEN)", example = "[\"REFRIGERATED\", \"FROZEN\"]")
        val supportedColdChainTypes: Set<ColdChainType>,
        @field:Schema(description = "월 처리 가능 물량", example = "10000")
        val maxMonthlyVolume: Int?,
    ) : AgencyResponse {
        companion object {
            fun from(result: AgencyResult): Detail {
                return Detail(
                    agencyId = result.agencyId.toString(),
                    userId = result.userId.toString(),
                    carrier = result.carrier.name,
                    agencyName = result.agencyName,
                    businessRegistrationNumber = result.businessRegistrationNumber,
                    representativeName = result.representativeName,
                    phoneNumber = result.phoneNumber,
                    postalCode = result.postalCode,
                    address = result.address,
                    addressDetail = result.addressDetail,
                    mainRegion = result.mainRegion,
                    serviceRegions = result.serviceRegions,
                    weekdayPickupStartTime = result.weekdayPickupStartTime,
                    weekdayPickupEndTime = result.weekdayPickupEndTime,
                    saturdayPickupAvailable = result.saturdayPickupAvailable,
                    saturdayDeliveryAvailable = result.saturdayDeliveryAvailable,
                    returnAvailable = result.returnAvailable,
                    supportedColdChainTypes = result.supportedColdChainTypes,
                    maxMonthlyVolume = result.maxMonthlyVolume,
                )
            }
        }
    }

    @Schema(name = "AgencySummaryResponse")
    data class Summary(
        @field:Schema(description = "대리점 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val agencyId: String,
        @field:Schema(description = "택배사", example = "CJ")
        val carrier: String,
        @field:Schema(description = "대리점명", example = "CJ 일동대리점")
        val agencyName: String,
        @field:Schema(description = "주 담당 지역", example = "경기도 안산시 일동")
        val mainRegion: String,
        @field:Schema(description = "담당 가능 지역")
        val serviceRegions: kotlin.collections.List<String>,
        @field:Schema(description = "평일 픽업 시작 시간", example = "09:00")
        val weekdayPickupStartTime: String?,
        @field:Schema(description = "평일 픽업 종료 시간", example = "18:00")
        val weekdayPickupEndTime: String?,
        @field:Schema(description = "토요일 집하 가능 여부", example = "true")
        val saturdayPickupAvailable: Boolean,
        @field:Schema(description = "토요일 배송 가능 여부", example = "true")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 처리 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "지원 콜드체인 타입 목록 (NONE, REFRIGERATED, FROZEN)", example = "[\"REFRIGERATED\", \"FROZEN\"]")
        val supportedColdChainTypes: Set<ColdChainType>,
        @field:Schema(description = "월 처리 가능 물량", example = "10000")
        val maxMonthlyVolume: Int?,
    ) : AgencyResponse {
        companion object {
            fun from(result: AgencyResult): Summary {
                return Summary(
                    agencyId = result.agencyId.toString(),
                    carrier = result.carrier.name,
                    agencyName = result.agencyName,
                    mainRegion = result.mainRegion,
                    serviceRegions = result.serviceRegions,
                    weekdayPickupStartTime = result.weekdayPickupStartTime,
                    weekdayPickupEndTime = result.weekdayPickupEndTime,
                    saturdayPickupAvailable = result.saturdayPickupAvailable,
                    saturdayDeliveryAvailable = result.saturdayDeliveryAvailable,
                    returnAvailable = result.returnAvailable,
                    supportedColdChainTypes = result.supportedColdChainTypes,
                    maxMonthlyVolume = result.maxMonthlyVolume,
                )
            }
        }
    }

    @Schema(name = "AgencyListResponse")
    data class List(
        @field:Schema(description = "대리점 목록")
        val items: kotlin.collections.List<Summary>,
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
    ) : AgencyResponse {
        companion object {
            fun from(results: Page<AgencyResult>): List {
                val page = PageResponse.from(results, Summary::from)

                return List(
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
