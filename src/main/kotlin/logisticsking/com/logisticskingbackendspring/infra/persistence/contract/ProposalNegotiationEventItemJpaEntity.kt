package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventItem
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "proposal_negotiation_event_items",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_proposal_negotiation_event_items_event_request_item",
            columnNames = ["proposal_negotiation_event_id", "contract_request_item_id"],
        ),
    ],
    indexes = [
        Index(name = "idx_proposal_negotiation_event_items_event_id", columnList = "proposal_negotiation_event_id"),
        Index(name = "idx_proposal_negotiation_event_items_request_item_id", columnList = "contract_request_item_id"),
    ],
)
class ProposalNegotiationEventItemJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "proposal_negotiation_event_id", columnDefinition = "BINARY(16)", nullable = false)
    val proposalNegotiationEventId: UUID,

    @Column(name = "contract_request_item_id", columnDefinition = "BINARY(16)", nullable = false)
    val contractRequestItemId: UUID,

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    val unitPrice: BigDecimal,
) : BaseJpaEntity() {

    fun toDomain(): ProposalNegotiationEventItem {
        return ProposalNegotiationEventItem.create(
            id = id,
            contractRequestItemId = contractRequestItemId,
            unitPrice = unitPrice,
        )
    }

    companion object {
        fun from(
            eventId: UUID,
            item: ProposalNegotiationEventItem,
        ): ProposalNegotiationEventItemJpaEntity {
            return ProposalNegotiationEventItemJpaEntity(
                id = item.id,
                proposalNegotiationEventId = eventId,
                contractRequestItemId = item.contractRequestItemId,
                unitPrice = item.unitPrice,
            )
        }
    }
}
