package logisticsking.com.logisticskingbackendspring.domain.deliver

import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class DeliverTest {

    @Test
    fun `create 성공 시 배송기사 정보를 정리한다`() {
        val deliverId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val agencyId = UUID.randomUUID()

        val deliver = deliver(
            id = deliverId,
            userId = userId,
            agencyId = agencyId,
            driverName = " 김택배 ",
            serviceRegions = listOf(" 경기도 안산시 일동 ", "경기도 안산시 본오동", "경기도 안산시 일동"),
        )

        assertEquals(deliverId, deliver.id)
        assertEquals(userId, deliver.userId)
        assertEquals(agencyId, deliver.agencyId)
        assertEquals("김택배", deliver.driverName)
        assertEquals(listOf("경기도 안산시 일동", "경기도 안산시 본오동"), deliver.serviceRegions)
    }

    @Test
    fun `canServe는 담당 가능 지역이면 true를 반환한다`() {
        val deliver = deliver(
            serviceRegions = listOf("경기도 안산시 일동", "경기도 안산시 본오동"),
        )

        assertTrue(deliver.canServe(" 경기도 안산시 일동 "))
        assertFalse(deliver.canServe("경기도 안산시 고잔동"))
    }

    @Test
    fun `update 성공 시 기존 식별자를 유지하고 정보를 변경한다`() {
        val deliver = deliver()
        val newAgencyId = UUID.randomUUID()

        val updated = deliver.update(
            agencyId = newAgencyId,
            driverName = "박배송",
            phoneNumber = "010-9876-5432",
            vehicleNumber = "34나5678",
            serviceRegions = listOf("경기도 안산시 사동"),
            active = false,
            memo = "오후 집하 담당",
        )

        assertEquals(deliver.id, updated.id)
        assertEquals(deliver.userId, updated.userId)
        assertEquals(newAgencyId, updated.agencyId)
        assertEquals("박배송", updated.driverName)
        assertEquals(false, updated.active)
    }

    @Test
    fun `create 시 기사명이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            deliver(driverName = " ")
        }

        assertEquals(DeliverErrorCode.INVALID_DRIVER_NAME, exception.errorCode)
    }

    @Test
    fun `create 시 담당 가능 지역이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            deliver(serviceRegions = emptyList())
        }

        assertEquals(DeliverErrorCode.INVALID_SERVICE_REGIONS, exception.errorCode)
    }

    private fun deliver(
        id: UUID = UUID.randomUUID(),
        userId: UUID = UUID.randomUUID(),
        agencyId: UUID = UUID.randomUUID(),
        driverName: String = "김택배",
        serviceRegions: List<String> = listOf("경기도 안산시 일동", "경기도 안산시 본오동"),
    ): Deliver {
        return Deliver.create(
            id = id,
            userId = userId,
            agencyId = agencyId,
            driverName = driverName,
            phoneNumber = "010-1234-5678",
            vehicleNumber = "12가3456",
            serviceRegions = serviceRegions,
            active = true,
            memo = "오전 집하 담당",
        )
    }
}
