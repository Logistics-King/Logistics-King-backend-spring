package logisticsking.com.logisticskingbackendspring.domain.deliver

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.util.UUID

class Deliver private constructor(
    val id: UUID,

    val userId: UUID,

    val employmentType: DeliverEmploymentType,

    val agencyId: UUID?,

    val driverName: String,

    val phoneNumber: String,

    val vehicleNumber: String?,

    val serviceRegions: List<String>,

    val active: Boolean,

    val memo: String?,
) {

    fun update(
        employmentType: DeliverEmploymentType,
        agencyId: UUID?,
        driverName: String,
        phoneNumber: String,
        vehicleNumber: String?,
        serviceRegions: List<String>,
        active: Boolean,
        memo: String?,
    ): Deliver {
        return create(
            id = id,
            userId = userId,
            employmentType = employmentType,
            agencyId = agencyId,
            driverName = driverName,
            phoneNumber = phoneNumber,
            vehicleNumber = vehicleNumber,
            serviceRegions = serviceRegions,
            active = active,
            memo = memo,
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
            employmentType: DeliverEmploymentType,
            agencyId: UUID?,
            driverName: String,
            phoneNumber: String,
            vehicleNumber: String?,
            serviceRegions: List<String>,
            active: Boolean,
            memo: String?,
        ): Deliver {
            requireDomain(driverName.isNotBlank(), DeliverErrorCode.INVALID_DRIVER_NAME)
            requireDomain(phoneNumber.isNotBlank(), DeliverErrorCode.INVALID_PHONE_NUMBER)
            requireDomain(
                serviceRegions.isNotEmpty() && serviceRegions.all { it.isNotBlank() },
                DeliverErrorCode.INVALID_SERVICE_REGIONS,
            )
            requireDomain(
                employmentType != DeliverEmploymentType.AGENCY_AFFILIATED || agencyId != null,
                DeliverErrorCode.AGENCY_REQUIRED_FOR_AFFILIATED_DELIVER,
            )

            return Deliver(
                id = id,
                userId = userId,
                employmentType = employmentType,
                agencyId = agencyId,
                driverName = driverName.trim(),
                phoneNumber = phoneNumber.trim(),
                vehicleNumber = vehicleNumber?.trim()?.takeIf { it.isNotBlank() },
                serviceRegions = serviceRegions.map { it.trim() }.distinct(),
                active = active,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
            )
        }

        fun restore(
            id: UUID,
            userId: UUID,
            employmentType: DeliverEmploymentType,
            agencyId: UUID?,
            driverName: String,
            phoneNumber: String,
            vehicleNumber: String?,
            serviceRegions: List<String>,
            active: Boolean,
            memo: String?,
        ): Deliver {
            return Deliver(
                id = id,
                userId = userId,
                employmentType = employmentType,
                agencyId = agencyId,
                driverName = driverName,
                phoneNumber = phoneNumber,
                vehicleNumber = vehicleNumber,
                serviceRegions = serviceRegions,
                active = active,
                memo = memo,
            )
        }
    }
}
