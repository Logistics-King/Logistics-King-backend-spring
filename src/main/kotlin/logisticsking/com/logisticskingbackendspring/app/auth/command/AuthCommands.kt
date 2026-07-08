package logisticsking.com.logisticskingbackendspring.app.auth.command

import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverEmploymentType
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.util.UUID

data class SignUpCommand(
    val loginId: String,
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val name: String,
    val role: UserRole,
    val vendorProfile: SignUpVendorProfileCommand? = null,
    val agencyProfile: SignUpAgencyProfileCommand? = null,
    val deliverProfile: SignUpDeliverProfileCommand? = null,
)

data class SignUpVendorProfileCommand(
    val businessName: String,
    val businessRegistrationNumber: String?,
    val representativeName: String,
    val phoneNumber: String,
    val postalCode: String?,
    val address: String,
    val addressDetail: String?,
    val mainRegion: String,
)

data class SignUpAgencyProfileCommand(
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

data class SignUpDeliverProfileCommand(
    val employmentType: DeliverEmploymentType,
    val agencyId: UUID?,
    val driverName: String,
    val phoneNumber: String,
    val vehicleNumber: String?,
    val serviceRegions: List<String>,
    val active: Boolean,
    val memo: String?,
)

data class LoginCommand(
    val loginId: String,
    val password: String,
)

data class RequestLoginIdRecoveryCommand(
    val name: String,
    val email: String,
)

data class RequestPasswordResetCommand(
    val loginId: String,
    val email: String,
)

data class ResetPasswordCommand(
    val token: String,
    val newPassword: String,
    val newPasswordConfirm: String,
)

data class RefreshTokenCommand(
    val refreshToken: String,
)

data class LogoutCommand(
    val refreshToken: String?,
)
