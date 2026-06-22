package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.util.UUID

data class ProposalItem(
    val id: UUID,
    val contractRequestItemId: UUID,
    val unitPrice: BigDecimal,
) {
    fun changeUnitPrice(unitPrice: BigDecimal): ProposalItem {
        return create(
            id = id,
            contractRequestItemId = contractRequestItemId,
            unitPrice = unitPrice,
        )
    }

    companion object {
        fun create(
            id: UUID,
            contractRequestItemId: UUID,
            unitPrice: BigDecimal,
        ): ProposalItem {
            requireDomain(unitPrice > BigDecimal.ZERO, ProposalErrorCode.INVALID_UNIT_PRICE)

            return ProposalItem(
                id = id,
                contractRequestItemId = contractRequestItemId,
                unitPrice = unitPrice,
            )
        }
    }
}
