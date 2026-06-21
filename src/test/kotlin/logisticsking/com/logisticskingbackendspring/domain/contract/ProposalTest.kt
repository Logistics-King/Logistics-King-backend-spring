package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
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
            coldChainType = ColdChainType.NONE,
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
    fun `accept 성공 시 ACCEPTED 상태로 변경한다`() {
        val proposal = proposal()

        val accepted = proposal.accept()

        assertEquals(proposal.id, accepted.id)
        assertEquals(ProposalStatus.ACCEPTED, accepted.status)
    }

    @Test
    fun `reject 성공 시 REJECTED 상태로 변경한다`() {
        val proposal = proposal()

        val rejected = proposal.reject()

        assertEquals(proposal.id, rejected.id)
        assertEquals(ProposalStatus.REJECTED, rejected.status)
    }

    @Test
    fun `가격 협상 시작 시 최신 단가를 바꾸고 pending 이벤트를 기록한다`() {
        val proposal = proposal()
        val eventId = UUID.randomUUID()

        val negotiating = proposal.startPriceNegotiation(
            eventId = eventId,
            unitPrice = BigDecimal("1980"),
        )

        assertEquals(ProposalStatus.NEGOTIATING, negotiating.status)
        assertEquals(BigDecimal("1980"), negotiating.unitPrice)
        assertEquals(BigDecimal("2050"), negotiating.initialUnitPrice)
        assertNull(negotiating.finalUnitPrice)
        assertEquals(eventId, negotiating.pendingNegotiationId)
        assertEquals(2, negotiating.nextSequence)
    }

    @Test
    fun `pending 협상 수락 시 최종 단가를 확정하고 pending을 제거한다`() {
        val eventId = UUID.randomUUID()
        val negotiating = proposal().startPriceNegotiation(
            eventId = eventId,
            unitPrice = BigDecimal("1980"),
        )

        val accepted = negotiating.acceptPendingNegotiation(
            pendingEventId = eventId,
            unitPrice = BigDecimal("1980"),
        )

        assertEquals(ProposalStatus.NEGOTIATING, accepted.status)
        assertEquals(BigDecimal("1980"), accepted.unitPrice)
        assertEquals(BigDecimal("1980"), accepted.finalUnitPrice)
        assertNull(accepted.pendingNegotiationId)
        assertEquals(3, accepted.nextSequence)
    }

    @Test
    fun `pending 협상이 있으면 최종 제안 수락을 막는다`() {
        val negotiating = proposal().startPriceNegotiation(
            eventId = UUID.randomUUID(),
            unitPrice = BigDecimal("1980"),
        )

        val exception = assertThrows(GlobalException::class.java) {
            negotiating.accept()
        }

        assertEquals(ProposalErrorCode.PROPOSAL_HAS_PENDING_NEGOTIATION, exception.errorCode)
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
    fun `제출 상태가 아닌 제안은 수정할 수 없다`() {
        val withdrawn = proposal().withdraw()

        val exception = assertThrows(GlobalException::class.java) {
            withdrawn.update(
                unitPrice = BigDecimal("1980"),
                pickupStartTime = "09:30",
                pickupEndTime = "16:30",
                saturdayDeliveryAvailable = false,
                returnAvailable = true,
                coldChainType = ColdChainType.NONE,
                memo = null,
            )
        }

        assertEquals(ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_UPDATED, exception.errorCode)
    }

    @Test
    fun `철회된 제안은 수락할 수 없다`() {
        val withdrawn = proposal().withdraw()

        val exception = assertThrows(GlobalException::class.java) {
            withdrawn.accept()
        }

        assertEquals(ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_ACCEPTED, exception.errorCode)
    }

    @Test
    fun `수락된 제안은 철회할 수 없다`() {
        val accepted = proposal().accept()

        val exception = assertThrows(GlobalException::class.java) {
            accepted.withdraw()
        }

        assertEquals(ProposalErrorCode.ONLY_SUBMITTED_PROPOSAL_CAN_BE_WITHDRAWN, exception.errorCode)
    }

    @Test
    fun `협상 이벤트는 pending 가격 제안만 수락 또는 거절할 수 있다`() {
        val event = ProposalNegotiationEvent.priceOffer(
            id = UUID.randomUUID(),
            proposalId = UUID.randomUUID(),
            sequence = 1,
            actorType = ContractPartyType.AGENCY,
            unitPrice = BigDecimal("1980"),
            memo = "조율 제안",
        )

        assertEquals(ProposalNegotiationEventStatus.ACCEPTED, event.accept().status)
        assertEquals(ProposalNegotiationEventStatus.REJECTED, event.reject().status)

        val recorded = ProposalNegotiationEvent.recorded(
            id = UUID.randomUUID(),
            proposalId = event.proposalId,
            sequence = 2,
            actorType = ContractPartyType.VENDOR,
            eventType = ProposalNegotiationEventType.PRICE_ACCEPTED,
            memo = null,
        )
        val exception = assertThrows(GlobalException::class.java) {
            recorded.accept()
        }

        assertEquals(ProposalErrorCode.NEGOTIATION_EVENT_IS_NOT_PENDING, exception.errorCode)
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
            coldChainType = ColdChainType.NONE,
            memo = memo,
        )
    }
}
