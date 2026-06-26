package logisticsking.com.logisticskingbackendspring.app.recommendation.result

import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.recommendation.RecommendationPurpose
import logisticsking.com.logisticskingbackendspring.domain.recommendation.RecommendationReasonType
import logisticsking.com.logisticskingbackendspring.domain.recommendation.RecommendationTargetType
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
