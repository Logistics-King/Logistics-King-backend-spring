package logisticsking.com.logisticskingbackendspring.app.agency.command

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import java.util.UUID

data class CreateAgencyCommand(
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
)

data class UpdateAgencyCommand(
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
)
