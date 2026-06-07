package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ProposalTest {

    @Test
    fun `create 성공 시 제안 정보를 정리하고 SUBMITTED 상태로 생성한다`() {
        val proposalId = UUID.randomUUID()
        val contractRequestId = UUID.randomUUID()
        val vendorId = UUID.randomUUID()
        val agencyId = UUID.randomUUID()

        val proposal = proposal(
            id = proposalId,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            pickupStartTime = " 10:00 ",
            pickupEndTime = " 17:00 ",
            memo = " ",
        )

        assertEquals(proposalId, proposal.id)
        assertEquals(contractRequestId, proposal.contractRequestId)
        assertEquals(vendorId, proposal.vendorId)
        assertEquals(agencyId, proposal.agencyId)
        assertEquals("10:00", proposal.pickupStartTime)
        assertEquals("17:00", proposal.pickupEndTime)
        assertNull(proposal.memo)
        assertEquals(ProposalStatus.SUBMITTED, proposal.status)
    }

    @Test
    fun `update 성공 시 기존 식별자를 유지하고 정보를 변경한다`() {
        val proposal = proposal()

        val updated = proposal.update(
            unitPrice = BigDecimal("1980"),
            pickupStartTime = "09:30",
            pickupEndTime = "16:30",
            saturdayDeliveryAvailable = false,
            returnAvailable = true,
            coldChainAvailable = false,
            memo = "오전 집하 기준 단가 조정",
        )

        assertEquals(proposal.id, updated.id)
        assertEquals(proposal.contractRequestId, updated.contractRequestId)
        assertEquals(BigDecimal("1980"), updated.unitPrice)
        assertEquals("09:30", updated.pickupStartTime)
        assertEquals(false, updated.saturdayDeliveryAvailable)
        assertEquals("오전 집하 기준 단가 조정", updated.memo)
    }

    @Test
    fun `withdraw 성공 시 WITHDRAWN 상태로 변경한다`() {
        val proposal = proposal()

        val withdrawn = proposal.withdraw()

        assertEquals(proposal.id, withdrawn.id)
        assertEquals(ProposalStatus.WITHDRAWN, withdrawn.status)
    }

    @Test
    fun `create 시 단가가 1보다 작으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            proposal(unitPrice = BigDecimal.ZERO)
        }

        assertEquals(ProposalErrorCode.INVALID_UNIT_PRICE, exception.errorCode)
    }

    @Test
    fun `create 시 픽업 시간이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            proposal(pickupStartTime = " ")
        }

        assertEquals(ProposalErrorCode.INVALID_PICKUP_TIME, exception.errorCode)
    }

    @Test
    fun `철회된 제안은 수정할 수 없다`() {
        val withdrawn = proposal().withdraw()

        val exception = assertThrows(GlobalException::class.java) {
            withdrawn.update(
                unitPrice = BigDecimal("1980"),
                pickupStartTime = "09:30",
                pickupEndTime = "16:30",
                saturdayDeliveryAvailable = false,
                returnAvailable = true,
                coldChainAvailable = false,
                memo = null,
            )
        }

        assertEquals(ProposalErrorCode.WITHDRAWN_PROPOSAL_CANNOT_BE_UPDATED, exception.errorCode)
    }

    private fun proposal(
        id: UUID = UUID.randomUUID(),
        contractRequestId: UUID = UUID.randomUUID(),
        vendorId: UUID = UUID.randomUUID(),
        agencyId: UUID = UUID.randomUUID(),
        unitPrice: BigDecimal = BigDecimal("2050"),
        pickupStartTime: String = "10:00",
        pickupEndTime: String = "17:00",
        memo: String? = "의류 800박스 기준 집하 가능",
    ): Proposal {
        return Proposal.create(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = true,
            returnAvailable = true,
            coldChainAvailable = false,
            memo = memo,
        )
    }
}
