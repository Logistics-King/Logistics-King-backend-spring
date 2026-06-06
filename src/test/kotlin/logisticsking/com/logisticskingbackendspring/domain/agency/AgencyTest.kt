package logisticsking.com.logisticskingbackendspring.domain.agency

import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class AgencyTest {

    @Test
    fun `create 성공 시 대리점 정보를 정리한다`() {
        val agencyId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        val agency = agency(
            id = agencyId,
            userId = userId,
            agencyName = " CJ 일동대리점 ",
            serviceRegions = listOf(" 경기도 안산시 일동 ", "경기도 안산시 본오동", "경기도 안산시 일동"),
        )

        assertEquals(agencyId, agency.id)
        assertEquals(userId, agency.userId)
        assertEquals(Carrier.CJ, agency.carrier)
        assertEquals("CJ 일동대리점", agency.agencyName)
        assertEquals(listOf("경기도 안산시 일동", "경기도 안산시 본오동"), agency.serviceRegions)
    }

    @Test
    fun `canServe는 담당 가능 지역이면 true를 반환한다`() {
        val agency = agency(
            serviceRegions = listOf("경기도 안산시 일동", "경기도 안산시 본오동"),
        )

        assertTrue(agency.canServe(" 경기도 안산시 일동 "))
        assertFalse(agency.canServe("경기도 안산시 고잔동"))
    }

    @Test
    fun `update 성공 시 기존 식별자를 유지하고 정보를 변경한다`() {
        val agency = agency()

        val updated = agency.update(
            carrier = Carrier.HANJIN,
            agencyName = "한진 사동대리점",
            businessRegistrationNumber = "987-65-43210",
            representativeName = "박대표",
            phoneNumber = "010-9876-5432",
            postalCode = "15500",
            address = "경기도 안산시 상록구 사동",
            addressDetail = "2층",
            mainRegion = "경기도 안산시 사동",
            serviceRegions = listOf("경기도 안산시 사동"),
            weekdayPickupStartTime = "10:00",
            weekdayPickupEndTime = "17:00",
            saturdayPickupAvailable = false,
            saturdayDeliveryAvailable = false,
            returnAvailable = true,
            coldChainAvailable = false,
            maxMonthlyVolume = 5000,
        )

        assertEquals(agency.id, updated.id)
        assertEquals(agency.userId, updated.userId)
        assertEquals(Carrier.HANJIN, updated.carrier)
        assertEquals("한진 사동대리점", updated.agencyName)
        assertEquals("경기도 안산시 사동", updated.mainRegion)
        assertEquals(5000, updated.maxMonthlyVolume)
    }

    @Test
    fun `create 시 대리점명이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            agency(agencyName = " ")
        }

        assertEquals(AgencyErrorCode.INVALID_AGENCY_NAME, exception.errorCode)
    }

    @Test
    fun `create 시 담당 가능 지역이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            agency(serviceRegions = emptyList())
        }

        assertEquals(AgencyErrorCode.INVALID_SERVICE_REGIONS, exception.errorCode)
    }

    @Test
    fun `create 시 월 처리 가능 물량이 음수면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            agency(maxMonthlyVolume = -1)
        }

        assertEquals(AgencyErrorCode.INVALID_MAX_MONTHLY_VOLUME, exception.errorCode)
    }

    private fun agency(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        carrier: Carrier = Carrier.CJ,
        agencyName: String = "CJ 일동대리점",
        serviceRegions: List<String> = listOf("경기도 안산시 일동", "경기도 안산시 본오동"),
        maxMonthlyVolume: Int? = 10000,
    ): Agency {
        return Agency.create(
            id = id,
            userId = userId,
            carrier = carrier,
            agencyName = agencyName,
            businessRegistrationNumber = "123-45-67890",
            representativeName = "김대표",
            phoneNumber = "010-1234-5678",
            postalCode = "15360",
            address = "경기도 안산시 상록구 일동",
            addressDetail = "1층",
            mainRegion = "경기도 안산시 일동",
            serviceRegions = serviceRegions,
            weekdayPickupStartTime = "09:00",
            weekdayPickupEndTime = "18:00",
            saturdayPickupAvailable = true,
            saturdayDeliveryAvailable = true,
            returnAvailable = true,
            coldChainAvailable = false,
            maxMonthlyVolume = maxMonthlyVolume,
        )
    }
}
