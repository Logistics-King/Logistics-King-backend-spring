package logisticsking.com.logisticskingbackendspring.domain.delivercontract

import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class DeliverContractTest {

    @Test
    fun `create 성공 시 배송기사 계약 정보를 정리하고 REQUESTED 상태로 생성한다`() {
        val deliverContractId = UUID.randomUUID()
        val agencyId = UUID.randomUUID()
        val deliverId = UUID.randomUUID()

        val deliverContract = deliverContract(
            id = deliverContractId,
            agencyId = agencyId,
            deliverId = deliverId,
            serviceRegion = " 경기도 안산시 일동 ",
            memo = " ",
        )

        assertEquals(deliverContractId, deliverContract.id)
        assertEquals(agencyId, deliverContract.agencyId)
        assertEquals(deliverId, deliverContract.deliverId)
        assertEquals("경기도 안산시 일동", deliverContract.serviceRegion)
        assertNull(deliverContract.memo)
        assertEquals(DeliverContractStatus.REQUESTED, deliverContract.status)
    }

    @Test
    fun `update 성공 시 요청 상태 계약 정보를 변경한다`() {
        val deliverContract = deliverContract()

        val updated = deliverContract.update(
            serviceRegion = "경기도 안산시 본오동",
            expectedMonthlyVolume = 1000,
            unitPrice = BigDecimal("950"),
            startDate = LocalDate.of(2026, 6, 1),
            endDate = LocalDate.of(2026, 12, 31),
            memo = "본오동 물량 추가",
        )

        assertEquals(deliverContract.id, updated.id)
        assertEquals("경기도 안산시 본오동", updated.serviceRegion)
        assertEquals(1000, updated.expectedMonthlyVolume)
        assertEquals(BigDecimal("950"), updated.unitPrice)
        assertEquals("본오동 물량 추가", updated.memo)
    }

    @Test
    fun `accept 성공 시 ACCEPTED 상태로 변경한다`() {
        val accepted = deliverContract().accept()

        assertEquals(DeliverContractStatus.ACCEPTED, accepted.status)
    }

    @Test
    fun `reject 성공 시 REJECTED 상태로 변경한다`() {
        val rejected = deliverContract().reject()

        assertEquals(DeliverContractStatus.REJECTED, rejected.status)
    }

    @Test
    fun `cancel 성공 시 CANCELLED 상태로 변경한다`() {
        val cancelled = deliverContract().cancel()

        assertEquals(DeliverContractStatus.CANCELLED, cancelled.status)
    }

    @Test
    fun `create 시 담당 지역이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            deliverContract(serviceRegion = " ")
        }

        assertEquals(DeliverContractErrorCode.INVALID_SERVICE_REGION, exception.errorCode)
    }

    @Test
    fun `create 시 예상 월 물량이 1보다 작으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            deliverContract(expectedMonthlyVolume = 0)
        }

        assertEquals(DeliverContractErrorCode.INVALID_EXPECTED_MONTHLY_VOLUME, exception.errorCode)
    }

    @Test
    fun `create 시 건당 단가가 0보다 작거나 같으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            deliverContract(unitPrice = BigDecimal.ZERO)
        }

        assertEquals(DeliverContractErrorCode.INVALID_UNIT_PRICE, exception.errorCode)
    }

    @Test
    fun `create 시 종료일이 시작일보다 빠르면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            deliverContract(
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 5, 31),
            )
        }

        assertEquals(DeliverContractErrorCode.INVALID_DATE_RANGE, exception.errorCode)
    }

    @Test
    fun `수락된 계약은 수정할 수 없다`() {
        val accepted = deliverContract().accept()

        val exception = assertThrows(GlobalException::class.java) {
            accepted.update(
                serviceRegion = "경기도 안산시 본오동",
                expectedMonthlyVolume = 1000,
                unitPrice = BigDecimal("950"),
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 12, 31),
                memo = null,
            )
        }

        assertEquals(DeliverContractErrorCode.ONLY_REQUESTED_CONTRACT_CAN_BE_UPDATED, exception.errorCode)
    }

    private fun deliverContract(
        id: UUID = UUID.randomUUID(),
        agencyId: UUID = UUID.randomUUID(),
        deliverId: UUID = UUID.randomUUID(),
        serviceRegion: String = "경기도 안산시 일동",
        expectedMonthlyVolume: Int = 800,
        unitPrice: BigDecimal = BigDecimal("900"),
        startDate: LocalDate = LocalDate.of(2026, 6, 1),
        endDate: LocalDate? = LocalDate.of(2026, 12, 31),
        memo: String? = "일동 의류 물량 오전 집하 담당",
    ): DeliverContract {
        return DeliverContract.create(
            id = id,
            agencyId = agencyId,
            deliverId = deliverId,
            serviceRegion = serviceRegion,
            expectedMonthlyVolume = expectedMonthlyVolume,
            unitPrice = unitPrice,
            startDate = startDate,
            endDate = endDate,
            memo = memo,
        )
    }
}
