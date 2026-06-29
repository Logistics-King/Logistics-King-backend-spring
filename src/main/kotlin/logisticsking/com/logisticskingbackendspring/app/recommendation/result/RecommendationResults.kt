package logisticsking.com.logisticskingbackendspring.app.recommendation.result

import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.recommendation.RecommendationPurpose
import logisticsking.com.logisticskingbackendspring.domain.recommendation.RecommendationReasonType
import logisticsking.com.logisticskingbackendspring.domain.recommendation.RecommendationTargetType
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import java.util.UUID

data class AgencyRecommendationResult(
    val targetType: RecommendationTargetType,
    val purpose: RecommendationPurpose,
    val agencyId: UUID,
    val carrier: Carrier,
    val agencyName: String,
    val mainRegion: String,
    val serviceRegions: List<String>,
    val address: String,
    val addressDetail: String?,
    val weekdayPickupStartTime: String?,
    val weekdayPickupEndTime: String?,
    val saturdayPickupAvailable: Boolean,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val supportedColdChainTypes: Set<ColdChainType>,
    val maxMonthlyVolume: Int?,
    val score: Int,
    val reasons: List<RecommendationReasonResult>,
) {
    companion object {
        fun from(
            agency: Agency,
            score: Int,
            reasons: List<RecommendationReasonResult>,
        ): AgencyRecommendationResult {
            return AgencyRecommendationResult(
                targetType = RecommendationTargetType.AGENCY,
                purpose = RecommendationPurpose.CONTRACT_REQUEST_AGENCY_MATCHING,
                agencyId = agency.id,
                carrier = agency.carrier,
                agencyName = agency.agencyName,
                mainRegion = agency.mainRegion,
                serviceRegions = agency.serviceRegions,
                address = agency.address,
                addressDetail = agency.addressDetail,
                weekdayPickupStartTime = agency.weekdayPickupStartTime,
                weekdayPickupEndTime = agency.weekdayPickupEndTime,
                saturdayPickupAvailable = agency.saturdayPickupAvailable,
                saturdayDeliveryAvailable = agency.saturdayDeliveryAvailable,
                returnAvailable = agency.returnAvailable,
                supportedColdChainTypes = agency.supportedColdChainTypes,
                maxMonthlyVolume = agency.maxMonthlyVolume,
                score = score,
                reasons = reasons,
            )
        }
    }
}

data class RecommendationReasonResult(
    val type: RecommendationReasonType,
    val label: String = type.label,
)

data class VendorRecommendationResult(
    val targetType: RecommendationTargetType,
    val purpose: RecommendationPurpose,
    val vendorId: UUID,
    val businessName: String,
    val representativeName: String,
    val phoneNumber: String,
    val address: String,
    val addressDetail: String?,
    val mainRegion: String,
    val score: Int,
    val reasons: List<RecommendationReasonResult>,
) {
    companion object {
        fun from(
            vendor: Vendor,
            score: Int,
            reasons: List<RecommendationReasonResult>,
        ): VendorRecommendationResult {
            return VendorRecommendationResult(
                targetType = RecommendationTargetType.VENDOR,
                purpose = RecommendationPurpose.AGENCY_SALES_VENDOR_DISCOVERY,
                vendorId = vendor.id,
                businessName = vendor.businessName,
                representativeName = vendor.representativeName,
                phoneNumber = vendor.phoneNumber,
                address = vendor.address,
                addressDetail = vendor.addressDetail,
                mainRegion = vendor.mainRegion,
                score = score,
                reasons = reasons,
            )
        }
    }
}
