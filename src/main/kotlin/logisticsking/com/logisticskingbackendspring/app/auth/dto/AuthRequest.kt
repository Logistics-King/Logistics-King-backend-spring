package logisticsking.com.logisticskingbackendspring.app.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import logisticsking.com.logisticskingbackendspring.app.auth.command.LoginCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RequestLoginIdRecoveryCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.RequestPasswordResetCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.ResetPasswordCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpAgencyProfileCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpDeliverProfileCommand
import logisticsking.com.logisticskingbackendspring.app.auth.command.SignUpVendorProfileCommand
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverEmploymentType
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.util.UUID

@Schema(description = "인증 요청")
sealed interface AuthRequest {
    data class SignUpVendor(
        @field:Schema(description = "로그인 ID", example = "vendor01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,

        @field:Schema(description = "이메일", example = "vendor01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,

        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,

        @field:Schema(description = "비밀번호 확인", example = "password1234")
        @field:NotBlank(message = "passwordConfirm은 필수입니다.")
        val passwordConfirm: String?,

        @field:Schema(description = "이름", example = "서울 옷가게")
        @field:NotBlank(message = "name은 필수입니다.")
        val name: String?,

        @field:Schema(description = "상호명", example = "안산 옷가게")
        @field:NotBlank(message = "businessName은 필수입니다.")
        val businessName: String?,

        @field:Schema(description = "사업자등록번호", example = "123-45-67890")
        val businessRegistrationNumber: String?,

        @field:Schema(description = "대표자명", example = "김사장")
        @field:NotBlank(message = "representativeName은 필수입니다.")
        val representativeName: String?,

        @field:Schema(description = "연락처", example = "010-1234-5678")
        @field:NotBlank(message = "phoneNumber는 필수입니다.")
        val phoneNumber: String?,

        @field:Schema(description = "우편번호", example = "15360")
        val postalCode: String?,

        @field:Schema(description = "사업장 주소", example = "경기도 안산시 상록구 일동")
        @field:NotBlank(message = "address는 필수입니다.")
        val address: String?,

        @field:Schema(description = "상세 주소", example = "101호")
        val addressDetail: String?,

        @field:Schema(description = "주 발송 지역", example = "경기도 안산시 일동")
        @field:NotBlank(message = "mainRegion은 필수입니다.")
        val mainRegion: String?,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId.orEmpty(),
                email = email.orEmpty(),
                password = password.orEmpty(),
                passwordConfirm = passwordConfirm.orEmpty(),
                name = name.orEmpty(),
                role = UserRole.VENDOR,
                vendorProfile = SignUpVendorProfileCommand(
                    businessName = businessName.orEmpty(),
                    businessRegistrationNumber = businessRegistrationNumber,
                    representativeName = representativeName.orEmpty(),
                    phoneNumber = phoneNumber.orEmpty(),
                    postalCode = postalCode,
                    address = address.orEmpty(),
                    addressDetail = addressDetail,
                    mainRegion = mainRegion.orEmpty(),
                ),
            )
        }
    }

    data class SignUpAgency(
        @field:Schema(description = "로그인 ID", example = "agency01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,

        @field:Schema(description = "이메일", example = "agency01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,

        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,

        @field:Schema(description = "비밀번호 확인", example = "password1234")
        @field:NotBlank(message = "passwordConfirm은 필수입니다.")
        val passwordConfirm: String?,

        @field:Schema(description = "이름", example = "CJ 서울 대리점")
        @field:NotBlank(message = "name은 필수입니다.")
        val name: String?,

        @field:Schema(description = "택배사", example = "CJ")
        val carrier: Carrier,

        @field:Schema(description = "대리점명", example = "CJ 일동대리점")
        @field:NotBlank(message = "agencyName은 필수입니다.")
        val agencyName: String?,

        @field:Schema(description = "사업자등록번호", example = "123-45-67890")
        val businessRegistrationNumber: String?,

        @field:Schema(description = "대표자명", example = "김대표")
        @field:NotBlank(message = "representativeName은 필수입니다.")
        val representativeName: String?,

        @field:Schema(description = "연락처", example = "010-1234-5678")
        @field:NotBlank(message = "phoneNumber는 필수입니다.")
        val phoneNumber: String?,

        @field:Schema(description = "우편번호", example = "15360")
        val postalCode: String?,

        @field:Schema(description = "대리점 주소", example = "경기도 안산시 상록구 일동")
        @field:NotBlank(message = "address는 필수입니다.")
        val address: String?,

        @field:Schema(description = "상세 주소", example = "1층")
        val addressDetail: String?,

        @field:Schema(description = "주 담당 지역", example = "경기도 안산시 일동")
        @field:NotBlank(message = "mainRegion은 필수입니다.")
        val mainRegion: String?,

        @field:Schema(description = "담당 가능 지역", example = "[\"경기도 안산시 일동\", \"경기도 안산시 본오동\"]")
        val serviceRegions: List<String>,

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

        @field:Schema(description = "지원 콜드체인 타입 목록 (NONE, REFRIGERATED, FROZEN)", example = "[\"REFRIGERATED\", \"FROZEN\"]")
        val supportedColdChainTypes: Set<ColdChainType>,

        @field:Schema(description = "월 처리 가능 물량", example = "10000")
        val maxMonthlyVolume: Int?,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId.orEmpty(),
                email = email.orEmpty(),
                password = password.orEmpty(),
                passwordConfirm = passwordConfirm.orEmpty(),
                name = name.orEmpty(),
                role = UserRole.AGENCY,
                agencyProfile = SignUpAgencyProfileCommand(
                    carrier = carrier,
                    agencyName = agencyName.orEmpty(),
                    businessRegistrationNumber = businessRegistrationNumber,
                    representativeName = representativeName.orEmpty(),
                    phoneNumber = phoneNumber.orEmpty(),
                    postalCode = postalCode,
                    address = address.orEmpty(),
                    addressDetail = addressDetail,
                    mainRegion = mainRegion.orEmpty(),
                    serviceRegions = serviceRegions,
                    weekdayPickupStartTime = weekdayPickupStartTime,
                    weekdayPickupEndTime = weekdayPickupEndTime,
                    saturdayPickupAvailable = saturdayPickupAvailable,
                    saturdayDeliveryAvailable = saturdayDeliveryAvailable,
                    returnAvailable = returnAvailable,
                    supportedColdChainTypes = supportedColdChainTypes,
                    maxMonthlyVolume = maxMonthlyVolume,
                ),
            )
        }
    }

    data class SignUpDriver(
        @field:Schema(description = "로그인 ID", example = "driver01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,

        @field:Schema(description = "이메일", example = "driver01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,

        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,

        @field:Schema(description = "비밀번호 확인", example = "password1234")
        @field:NotBlank(message = "passwordConfirm은 필수입니다.")
        val passwordConfirm: String?,

        @field:Schema(description = "이름", example = "김택배")
        @field:NotBlank(message = "name은 필수입니다.")
        val name: String?,

        @field:Schema(description = "배송기사 고용 형태 (AGENCY_AFFILIATED, FREELANCER)", example = "AGENCY_AFFILIATED")
        val employmentType: DeliverEmploymentType,

        @field:Schema(description = "소속 대리점 ID. AGENCY_AFFILIATED일 때 필수이고 FREELANCER면 null 가능합니다.", example = "019b1f44-a741-7000-8000-000000000010")
        val agencyId: UUID?,

        @field:Schema(description = "기사명", example = "김택배")
        @field:NotBlank(message = "driverName은 필수입니다.")
        val driverName: String?,

        @field:Schema(description = "연락처", example = "010-1234-5678")
        @field:NotBlank(message = "phoneNumber는 필수입니다.")
        val phoneNumber: String?,

        @field:Schema(description = "차량번호", example = "12가3456")
        val vehicleNumber: String?,

        @field:Schema(description = "담당 가능 지역", example = "[\"경기도 안산시 일동\", \"경기도 안산시 본오동\"]")
        val serviceRegions: List<String>,

        @field:Schema(description = "운영 활성 여부", example = "true")
        val active: Boolean,

        @field:Schema(description = "메모", example = "오전 집하 담당")
        val memo: String?,
    ) : AuthRequest {
        fun toCommand(): SignUpCommand {
            return SignUpCommand(
                loginId = loginId.orEmpty(),
                email = email.orEmpty(),
                password = password.orEmpty(),
                passwordConfirm = passwordConfirm.orEmpty(),
                name = name.orEmpty(),
                role = UserRole.DRIVER,
                deliverProfile = SignUpDeliverProfileCommand(
                    employmentType = employmentType,
                    agencyId = agencyId,
                    driverName = driverName.orEmpty(),
                    phoneNumber = phoneNumber.orEmpty(),
                    vehicleNumber = vehicleNumber,
                    serviceRegions = serviceRegions,
                    active = active,
                    memo = memo,
                ),
            )
        }
    }

    @Schema(description = "로그인 요청")
    data class Login(
        @field:Schema(description = "로그인 ID", example = "vendor01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,

        @field:Schema(description = "비밀번호", example = "password1234")
        @field:NotBlank(message = "password는 필수입니다.")
        val password: String?,
    ) : AuthRequest {
        fun toCommand(): LoginCommand {
            return LoginCommand(
                loginId = loginId.orEmpty(),
                password = password.orEmpty(),
            )
        }
    }

    @Schema(description = "아이디 찾기 요청")
    data class RequestLoginIdRecovery(
        @field:Schema(description = "이름", example = "서울 옷가게")
        @field:NotBlank(message = "name은 필수입니다.")
        val name: String?,

        @field:Schema(description = "이메일", example = "vendor01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,
    ) : AuthRequest {
        fun toCommand(): RequestLoginIdRecoveryCommand {
            return RequestLoginIdRecoveryCommand(
                name = name.orEmpty(),
                email = email.orEmpty(),
            )
        }
    }

    @Schema(description = "비밀번호 재설정 인증 요청")
    data class RequestPasswordReset(
        @field:Schema(description = "로그인 ID", example = "vendor01")
        @field:NotBlank(message = "loginId는 필수입니다.")
        val loginId: String?,

        @field:Schema(description = "이메일", example = "vendor01@example.com")
        @field:NotBlank(message = "email은 필수입니다.")
        @field:Email(message = "email 형식이 올바르지 않습니다.")
        val email: String?,
    ) : AuthRequest {
        fun toCommand(): RequestPasswordResetCommand {
            return RequestPasswordResetCommand(
                loginId = loginId.orEmpty(),
                email = email.orEmpty(),
            )
        }
    }

    @Schema(description = "비밀번호 재설정 확정 요청")
    data class ResetPassword(
        @field:Schema(description = "비밀번호 재설정 토큰", example = "u2oHfT7QNo4sN6ltH3GSOdr20jhpIoQPBHb0Yw4CApc")
        @field:NotBlank(message = "token은 필수입니다.")
        val token: String?,

        @field:Schema(description = "새 비밀번호", example = "newPassword1234")
        @field:NotBlank(message = "newPassword는 필수입니다.")
        val newPassword: String?,

        @field:Schema(description = "새 비밀번호 확인", example = "newPassword1234")
        @field:NotBlank(message = "newPasswordConfirm은 필수입니다.")
        val newPasswordConfirm: String?,
    ) : AuthRequest {
        fun toCommand(): ResetPasswordCommand {
            return ResetPasswordCommand(
                token = token.orEmpty(),
                newPassword = newPassword.orEmpty(),
                newPasswordConfirm = newPasswordConfirm.orEmpty(),
            )
        }
    }
}
