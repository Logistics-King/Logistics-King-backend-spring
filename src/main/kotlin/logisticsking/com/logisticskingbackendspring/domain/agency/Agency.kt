package logisticsking.com.logisticskingbackendspring.domain.agency

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.util.UUID

class Agency private constructor(
    val id: UUID,

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

    fun update(
        carrier: Carrier,
        agencyName: String,
        businessRegistrationNumber: String?,
        representativeName: String,
        phoneNumber: String,
        postalCode: String?,
        address: String,
        addressDetail: String?,
        mainRegion: String,
        serviceRegions: List<String>,
        weekdayPickupStartTime: String?,
        weekdayPickupEndTime: String?,
        saturdayPickupAvailable: Boolean,
        saturdayDeliveryAvailable: Boolean,
        returnAvailable: Boolean,
        supportedColdChainTypes: Set<ColdChainType>,
        maxMonthlyVolume: Int?,
    ): Agency {
        return create(
            id = id,
            userId = userId,
            carrier = carrier,
            agencyName = agencyName,
            businessRegistrationNumber = businessRegistrationNumber,
            representativeName = representativeName,
            phoneNumber = phoneNumber,
            postalCode = postalCode,
            address = address,
            addressDetail = addressDetail,
            mainRegion = mainRegion,
            serviceRegions = serviceRegions,
            weekdayPickupStartTime = weekdayPickupStartTime,
            weekdayPickupEndTime = weekdayPickupEndTime,
            saturdayPickupAvailable = saturdayPickupAvailable,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            supportedColdChainTypes = supportedColdChainTypes,
            maxMonthlyVolume = maxMonthlyVolume,
        )
    }

    fun canServe(region: String): Boolean {
        val target = region.trim()
        return target.isNotBlank() && serviceRegions.any { it == target }
    }

    companion object {
        fun create(
            id: UUID,
            userId: UUID,
            carrier: Carrier,
            agencyName: String,
            businessRegistrationNumber: String?,
            representativeName: String,
            phoneNumber: String,
            postalCode: String?,
            address: String,
            addressDetail: String?,
            mainRegion: String,
            serviceRegions: List<String>,
            weekdayPickupStartTime: String?,
            weekdayPickupEndTime: String?,
            saturdayPickupAvailable: Boolean,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            supportedColdChainTypes: Set<ColdChainType>,
            maxMonthlyVolume: Int?,
        ): Agency {
            requireDomain(agencyName.isNotBlank(), AgencyErrorCode.INVALID_AGENCY_NAME)
            requireDomain(representativeName.isNotBlank(), AgencyErrorCode.INVALID_REPRESENTATIVE_NAME)
            requireDomain(phoneNumber.isNotBlank(), AgencyErrorCode.INVALID_PHONE_NUMBER)
            requireDomain(address.isNotBlank(), AgencyErrorCode.INVALID_ADDRESS)
            requireDomain(mainRegion.isNotBlank(), AgencyErrorCode.INVALID_MAIN_REGION)
            requireDomain(
                serviceRegions.isNotEmpty() && serviceRegions.all { it.isNotBlank() },
                AgencyErrorCode.INVALID_SERVICE_REGIONS,
            )
            requireDomain(
                maxMonthlyVolume == null || maxMonthlyVolume >= 0,
                AgencyErrorCode.INVALID_MAX_MONTHLY_VOLUME,
            )

            return Agency(
                id = id,
                userId = userId,
                carrier = carrier,
                agencyName = agencyName.trim(),
                businessRegistrationNumber = businessRegistrationNumber?.trim()?.takeIf { it.isNotBlank() },
                representativeName = representativeName.trim(),
                phoneNumber = phoneNumber.trim(),
                postalCode = postalCode?.trim()?.takeIf { it.isNotBlank() },
                address = address.trim(),
                addressDetail = addressDetail?.trim()?.takeIf { it.isNotBlank() },
                mainRegion = mainRegion.trim(),
                serviceRegions = serviceRegions.map { it.trim() }.distinct(),
                weekdayPickupStartTime = weekdayPickupStartTime?.trim()?.takeIf { it.isNotBlank() },
                weekdayPickupEndTime = weekdayPickupEndTime?.trim()?.takeIf { it.isNotBlank() },
                saturdayPickupAvailable = saturdayPickupAvailable,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                supportedColdChainTypes = normalizeColdChainTypes(supportedColdChainTypes),
                maxMonthlyVolume = maxMonthlyVolume,
            )
        }

        fun restore(
            id: UUID,
            userId: UUID,
            carrier: Carrier,
            agencyName: String,
            businessRegistrationNumber: String?,
            representativeName: String,
            phoneNumber: String,
            postalCode: String?,
            address: String,
            addressDetail: String?,
            mainRegion: String,
            serviceRegions: List<String>,
            weekdayPickupStartTime: String?,
            weekdayPickupEndTime: String?,
            saturdayPickupAvailable: Boolean,
            saturdayDeliveryAvailable: Boolean,
            returnAvailable: Boolean,
            supportedColdChainTypes: Set<ColdChainType>,
            maxMonthlyVolume: Int?,
        ): Agency {
            return Agency(
                id = id,
                userId = userId,
                carrier = carrier,
                agencyName = agencyName,
                businessRegistrationNumber = businessRegistrationNumber,
                representativeName = representativeName,
                phoneNumber = phoneNumber,
                postalCode = postalCode,
                address = address,
                addressDetail = addressDetail,
                mainRegion = mainRegion,
                serviceRegions = serviceRegions,
                weekdayPickupStartTime = weekdayPickupStartTime,
                weekdayPickupEndTime = weekdayPickupEndTime,
                saturdayPickupAvailable = saturdayPickupAvailable,
                saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                returnAvailable = returnAvailable,
                supportedColdChainTypes = normalizeColdChainTypes(supportedColdChainTypes),
                maxMonthlyVolume = maxMonthlyVolume,
            )
        }

        private fun normalizeColdChainTypes(supportedColdChainTypes: Set<ColdChainType>): Set<ColdChainType> {
            return supportedColdChainTypes
                .ifEmpty { setOf(ColdChainType.NONE) }
                .toSet()
        }
    }
}
