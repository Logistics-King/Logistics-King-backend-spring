package logisticsking.com.logisticskingbackendspring.app.recommendation.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.recommendation.result.AgencyRecommendationResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.result.RecommendationReasonResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.result.VendorRecommendationResult
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType

@Schema(description = "추천 응답")
sealed interface RecommendationResponse {
    @Schema(name = "RecommendedAgencyListResponse")
    data class RecommendedAgencies(
        @field:Schema(description = "추천 대리점 목록")
        val items: kotlin.collections.List<RecommendedAgency>,
    ) : RecommendationResponse {
        companion object {
            fun from(results: kotlin.collections.List<AgencyRecommendationResult>): RecommendedAgencies {
                return RecommendedAgencies(
                    items = results.map(RecommendedAgency::from),
                )
            }
        }
    }

    @Schema(name = "RecommendedVendorListResponse")
    data class RecommendedVendors(
        @field:Schema(description = "추천 화주 목록")
        val items: kotlin.collections.List<RecommendedVendor>,
    ) : RecommendationResponse {
        companion object {
            fun from(results: kotlin.collections.List<VendorRecommendationResult>): RecommendedVendors {
                return RecommendedVendors(
                    items = results.map(RecommendedVendor::from),
                )
            }
        }
    }

    @Schema(name = "RecommendedAgencyResponse")
    data class RecommendedAgency(
        @field:Schema(description = "추천 대상 타입", example = "AGENCY")
        val targetType: String,

        @field:Schema(description = "추천 목적", example = "CONTRACT_REQUEST_AGENCY_MATCHING")
        val purpose: String,

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

        @field:Schema(description = "대리점 주소", example = "경기도 안산시 상록구 일동")
        val address: String,

        @field:Schema(description = "상세 주소", example = "1층")
        val addressDetail: String?,

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

        @field:Schema(description = "지원 콜드체인 타입 목록", example = "[\"NONE\", \"REFRIGERATED\"]")
        val supportedColdChainTypes: Set<ColdChainType>,

        @field:Schema(description = "월 처리 가능 물량", example = "10000")
        val maxMonthlyVolume: Int?,

        @field:Schema(description = "추천 점수", example = "180")
        val score: Int,

        @field:Schema(description = "추천 사유 목록")
        val reasons: kotlin.collections.List<RecommendationReason>,
    ) : RecommendationResponse {
        companion object {
            fun from(result: AgencyRecommendationResult): RecommendedAgency {
                return RecommendedAgency(
                    targetType = result.targetType.name,
                    purpose = result.purpose.name,
                    agencyId = result.agencyId.toString(),
                    carrier = result.carrier.name,
                    agencyName = result.agencyName,
                    mainRegion = result.mainRegion,
                    serviceRegions = result.serviceRegions,
                    address = result.address,
                    addressDetail = result.addressDetail,
                    weekdayPickupStartTime = result.weekdayPickupStartTime,
                    weekdayPickupEndTime = result.weekdayPickupEndTime,
                    saturdayPickupAvailable = result.saturdayPickupAvailable,
                    saturdayDeliveryAvailable = result.saturdayDeliveryAvailable,
                    returnAvailable = result.returnAvailable,
                    supportedColdChainTypes = result.supportedColdChainTypes,
                    maxMonthlyVolume = result.maxMonthlyVolume,
                    score = result.score,
                    reasons = result.reasons.map(RecommendationReason::from),
                )
            }
        }
    }

    @Schema(name = "RecommendedVendorResponse")
    data class RecommendedVendor(
        @field:Schema(description = "추천 대상 타입", example = "VENDOR")
        val targetType: String,

        @field:Schema(description = "추천 목적", example = "AGENCY_SALES_VENDOR_DISCOVERY")
        val purpose: String,

        @field:Schema(description = "화주 ID", example = "019b1f44-a741-7000-8000-000000000001")
        val vendorId: String,

        @field:Schema(description = "화주 상호명", example = "안산 의류몰")
        val businessName: String,

        @field:Schema(description = "대표자명", example = "김화주")
        val representativeName: String,

        @field:Schema(description = "연락처", example = "010-1234-5678")
        val phoneNumber: String,

        @field:Schema(description = "화주 주소", example = "경기도 안산시 상록구 일동")
        val address: String,

        @field:Schema(description = "상세 주소", example = "101호")
        val addressDetail: String?,

        @field:Schema(description = "주요 지역", example = "경기도 안산시 일동")
        val mainRegion: String,

        @field:Schema(description = "추천 점수", example = "180")
        val score: Int,

        @field:Schema(description = "추천 사유 목록")
        val reasons: kotlin.collections.List<RecommendationReason>,
    ) : RecommendationResponse {
        companion object {
            fun from(result: VendorRecommendationResult): RecommendedVendor {
                return RecommendedVendor(
                    targetType = result.targetType.name,
                    purpose = result.purpose.name,
                    vendorId = result.vendorId.toString(),
                    businessName = result.businessName,
                    representativeName = result.representativeName,
                    phoneNumber = result.phoneNumber,
                    address = result.address,
                    addressDetail = result.addressDetail,
                    mainRegion = result.mainRegion,
                    score = result.score,
                    reasons = result.reasons.map(RecommendationReason::from),
                )
            }
        }
    }

    @Schema(name = "RecommendationReasonResponse")
    data class RecommendationReason(
        @field:Schema(description = "추천 사유 타입", example = "PREVIOUS_CONTRACT")
        val type: String,

        @field:Schema(description = "추천 사유 설명", example = "이전 계약 이력이 있는 대리점입니다.")
        val label: String,
    ) : RecommendationResponse {
        companion object {
            fun from(result: RecommendationReasonResult): RecommendationReason {
                return RecommendationReason(
                    type = result.type.name,
                    label = result.label,
                )
            }
        }
    }
}
