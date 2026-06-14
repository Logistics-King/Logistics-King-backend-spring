package logisticsking.com.logisticskingbackendspring.app.agency.dto

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.agency.command.CreateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.command.UpdateAgencyCommand
import logisticsking.com.logisticskingbackendspring.domain.agency.Carrier
import java.util.UUID

@Schema(description = "대리점 요청")
sealed interface AgencyRequest {
    @Schema(name = "AgencyCreateRequest")
    data class Create(
        @field:Schema(description = "택배사", example = "CJ")
        val carrier: Carrier,
        @field:Schema(description = "대리점명", example = "CJ 일동대리점")
        val agencyName: String,
        @field:Schema(description = "사업자등록번호", example = "123-45-67890")
        val businessRegistrationNumber: String?,
        @field:Schema(description = "대표자명", example = "김대표")
        val representativeName: String,
        @field:Schema(description = "연락처", example = "010-1234-5678")
        val phoneNumber: String,
        @field:Schema(description = "우편번호", example = "15360")
        val postalCode: String?,
        @field:Schema(description = "대리점 주소", example = "경기도 안산시 상록구 일동")
        val address: String,
        @field:Schema(description = "상세 주소", example = "1층")
        val addressDetail: String?,
        @field:Schema(description = "주 담당 지역", example = "경기도 안산시 일동")
        val mainRegion: String,
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
    ) : AgencyRequest {
        fun toCommand(userId: UUID): CreateAgencyCommand {
            return CreateAgencyCommand(
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
    }

    @Schema(name = "AgencyUpdateRequest")
    data class Update(
        @field:Schema(description = "택배사", example = "HANJIN")
        val carrier: Carrier,
        @field:Schema(description = "대리점명", example = "한진 사동대리점")
        val agencyName: String,
        @field:Schema(description = "사업자등록번호", example = "123-45-67890")
        val businessRegistrationNumber: String?,
        @field:Schema(description = "대표자명", example = "김대표")
        val representativeName: String,
        @field:Schema(description = "연락처", example = "010-9876-5432")
        val phoneNumber: String,
        @field:Schema(description = "우편번호", example = "15500")
        val postalCode: String?,
        @field:Schema(description = "대리점 주소", example = "경기도 안산시 상록구 사동")
        val address: String,
        @field:Schema(description = "상세 주소", example = "2층")
        val addressDetail: String?,
        @field:Schema(description = "주 담당 지역", example = "경기도 안산시 사동")
        val mainRegion: String,
        @field:Schema(description = "담당 가능 지역", example = "[\"경기도 안산시 사동\"]")
        val serviceRegions: List<String>,
        @field:Schema(description = "평일 픽업 시작 시간", example = "10:00")
        val weekdayPickupStartTime: String?,
        @field:Schema(description = "평일 픽업 종료 시간", example = "17:00")
        val weekdayPickupEndTime: String?,
        @field:Schema(description = "토요일 집하 가능 여부", example = "false")
        val saturdayPickupAvailable: Boolean,
        @field:Schema(description = "토요일 배송 가능 여부", example = "false")
        val saturdayDeliveryAvailable: Boolean,
        @field:Schema(description = "반품 처리 가능 여부", example = "true")
        val returnAvailable: Boolean,
        @field:Schema(description = "지원 콜드체인 타입 목록 (NONE, REFRIGERATED, FROZEN)", example = "[\"REFRIGERATED\", \"FROZEN\"]")
        val supportedColdChainTypes: Set<ColdChainType>,
        @field:Schema(description = "월 처리 가능 물량", example = "5000")
        val maxMonthlyVolume: Int?,
    ) : AgencyRequest {
        fun toCommand(userId: UUID): UpdateAgencyCommand {
            return UpdateAgencyCommand(
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
    }
}
