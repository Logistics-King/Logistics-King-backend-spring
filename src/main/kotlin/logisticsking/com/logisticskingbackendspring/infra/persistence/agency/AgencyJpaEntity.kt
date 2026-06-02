package logisticsking.com.logisticskingbackendspring.infra.persistence.agency

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.util.UUID

@Entity
@Table(name = "agencies")
class AgencyJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", nullable = false, length = 30)
    val carrier: Carrier,

    @Column(name = "agency_name", nullable = false, length = 100)
    val agencyName: String,

    @Column(name = "business_registration_number", length = 30)
    val businessRegistrationNumber: String?,

    @Column(name = "representative_name", nullable = false, length = 50)
    val representativeName: String,

    @Column(name = "phone_number", nullable = false, length = 30)
    val phoneNumber: String,

    @Column(name = "postal_code", length = 20)
    val postalCode: String?,

    @Column(name = "address", nullable = false, length = 255)
    val address: String,

    @Column(name = "address_detail", length = 255)
    val addressDetail: String?,

    @Column(name = "main_region", nullable = false, length = 100)
    val mainRegion: String,

    @Column(name = "service_regions", nullable = false, length = 500)
    val serviceRegions: String,

    @Column(name = "weekday_pickup_start_time", length = 10)
    val weekdayPickupStartTime: String?,

    @Column(name = "weekday_pickup_end_time", length = 10)
    val weekdayPickupEndTime: String?,

    @Column(name = "saturday_pickup_available", nullable = false)
    val saturdayPickupAvailable: Boolean,

    @Column(name = "saturday_delivery_available", nullable = false)
    val saturdayDeliveryAvailable: Boolean,

    @Column(name = "return_available", nullable = false)
    val returnAvailable: Boolean,

    @Column(name = "cold_chain_available", nullable = false)
    val coldChainAvailable: Boolean,

    @Column(name = "max_monthly_volume")
    val maxMonthlyVolume: Int?,
) : BaseJpaEntity() {

    fun toDomain(): Agency {
        return Agency.restore(
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
            serviceRegions = serviceRegions.split(SERVICE_REGION_DELIMITER)
                .map(String::trim)
                .filter(String::isNotBlank),
            weekdayPickupStartTime = weekdayPickupStartTime,
            weekdayPickupEndTime = weekdayPickupEndTime,
            saturdayPickupAvailable = saturdayPickupAvailable,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainAvailable = coldChainAvailable,
            maxMonthlyVolume = maxMonthlyVolume,
        )
    }

    companion object {
        private const val SERVICE_REGION_DELIMITER = "|"

        fun from(agency: Agency): AgencyJpaEntity {
            return AgencyJpaEntity(
                id = agency.id,
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
                serviceRegions = agency.serviceRegions.joinToString(SERVICE_REGION_DELIMITER),
                weekdayPickupStartTime = agency.weekdayPickupStartTime,
                weekdayPickupEndTime = agency.weekdayPickupEndTime,
                saturdayPickupAvailable = agency.saturdayPickupAvailable,
                saturdayDeliveryAvailable = agency.saturdayDeliveryAvailable,
                returnAvailable = agency.returnAvailable,
                coldChainAvailable = agency.coldChainAvailable,
                maxMonthlyVolume = agency.maxMonthlyVolume,
            )
        }
    }
}
