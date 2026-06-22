package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.util.UUID

data class ProposalNegotiationEventItem(
    val id: UUID,
    val contractRequestItemId: UUID,
    val unitPrice: BigDecimal,
) {
    companion object {
        fun create(
            id: UUID,
            contractRequestItemId: UUID,
            unitPrice: BigDecimal,
        ): ProposalNegotiationEventItem {
            requireDomain(unitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)

            return ProposalNegotiationEventItem(
                id = id,
                contractRequestItemId = contractRequestItemId,
                unitPrice = unitPrice,
            )
        }
    }
}
