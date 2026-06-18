package logisticsking.com.logisticskingbackendspring.app.agency.result

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import java.util.UUID

data class AgencyResult(
    val agencyId: UUID,
    val userId: UUID,
    val carrier: Carrier,
    val agencyName: String,
    val businessRegistrationNumber: String?,
    val representativeName: String,
    val phoneNumber: String,
    val postalCode: String?,
    val address: String,
    val addressDetail: String?,
    val mainRegion: String,
    val serviceRegions: List<String>,
    val weekdayPickupStartTime: String?,
    val weekdayPickupEndTime: String?,
    val saturdayPickupAvailable: Boolean,
    val saturdayDeliveryAvailable: Boolean,
    val returnAvailable: Boolean,
    val supportedColdChainTypes: Set<ColdChainType>,
    val maxMonthlyVolume: Int?,
) {
    companion object {
        fun from(agency: Agency): AgencyResult {
            return AgencyResult(
                agencyId = agency.id,
                userId = agency.userId,
                carrier = agency.carrier,
                agencyName = agency.agencyName,
                businessRegistrationNumber = agency.businessRegistrationNumber,
                representativeName = agency.representativeName,
                phoneNumber = agency.phoneNumber,
                postalCode = agency.postalCode,
                address = agency.address,
                addressDetail = agency.addressDetail,
                mainRegion = agency.mainRegion,
                serviceRegions = agency.serviceRegions,
                weekdayPickupStartTime = agency.weekdayPickupStartTime,
                weekdayPickupEndTime = agency.weekdayPickupEndTime,
                saturdayPickupAvailable = agency.saturdayPickupAvailable,
                saturdayDeliveryAvailable = agency.saturdayDeliveryAvailable,
                returnAvailable = agency.returnAvailable,
                supportedColdChainTypes = agency.supportedColdChainTypes,
                maxMonthlyVolume = agency.maxMonthlyVolume,
            )
        }
    }
}
