package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.util.UUID

class Vendor private constructor(
    val id: UUID,

    val userId: UUID,

    val businessName: String,

    val businessRegistrationNumber: String?,

    val representativeName: String,

    val phoneNumber: String,

    val postalCode: String?,

    val address: String,

    val addressDetail: String?,

    val mainRegion: String,
) {

    fun update(
        businessName: String,
        businessRegistrationNumber: String?,
        representativeName: String,
        phoneNumber: String,
        postalCode: String?,
        address: String,
        addressDetail: String?,
        mainRegion: String,
    ): Vendor {
        return create(
            id = id,
            userId = userId,
            businessName = businessName,
            businessRegistrationNumber = businessRegistrationNumber,
            representativeName = representativeName,
            phoneNumber = phoneNumber,
            postalCode = postalCode,
            address = address,
            addressDetail = addressDetail,
            mainRegion = mainRegion,
        )
    }

    companion object {
        fun create(
            id: UUID,
            userId: UUID,
            businessName: String,
            businessRegistrationNumber: String?,
            representativeName: String,
            phoneNumber: String,
            postalCode: String?,
            address: String,
            addressDetail: String?,
            mainRegion: String,
        ): Vendor {
            requireDomain(businessName.isNotBlank(), VendorErrorCode.INVALID_BUSINESS_NAME)
            requireDomain(representativeName.isNotBlank(), VendorErrorCode.INVALID_REPRESENTATIVE_NAME)
            requireDomain(phoneNumber.isNotBlank(), VendorErrorCode.INVALID_PHONE_NUMBER)
            requireDomain(address.isNotBlank(), VendorErrorCode.INVALID_ADDRESS)
            requireDomain(mainRegion.isNotBlank(), VendorErrorCode.INVALID_MAIN_REGION)

            return Vendor(
                id = id,
                userId = userId,
                businessName = businessName.trim(),
                businessRegistrationNumber = businessRegistrationNumber?.trim()?.takeIf { it.isNotBlank() },
                representativeName = representativeName.trim(),
                phoneNumber = phoneNumber.trim(),
                postalCode = postalCode?.trim()?.takeIf { it.isNotBlank() },
                address = address.trim(),
                addressDetail = addressDetail?.trim()?.takeIf { it.isNotBlank() },
                mainRegion = mainRegion.trim(),
            )
        }

        fun restore(
            id: UUID,
            userId: UUID,
            businessName: String,
            businessRegistrationNumber: String?,
            representativeName: String,
            phoneNumber: String,
            postalCode: String?,
            address: String,
            addressDetail: String?,
            mainRegion: String,
        ): Vendor {
            return Vendor(
                id = id,
                userId = userId,
                businessName = businessName,
                businessRegistrationNumber = businessRegistrationNumber,
                representativeName = representativeName,
                phoneNumber = phoneNumber,
                postalCode = postalCode,
                address = address,
                addressDetail = addressDetail,
                mainRegion = mainRegion,
            )
        }
    }
}
