package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalItem
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "proposal_items",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_proposal_items_proposal_request_item",
            columnNames = ["proposal_id", "contract_request_item_id"],
        ),
    ],
    indexes = [
        Index(name = "idx_proposal_items_proposal_id", columnList = "proposal_id"),
        Index(name = "idx_proposal_items_contract_request_item_id", columnList = "contract_request_item_id"),
    ],
)
class ProposalItemJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "proposal_id", columnDefinition = "BINARY(16)", nullable = false)
    val proposalId: UUID,

    @Column(name = "contract_request_item_id", columnDefinition = "BINARY(16)", nullable = false)
    val contractRequestItemId: UUID,

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    val unitPrice: BigDecimal,
) : BaseJpaEntity() {

    fun toDomain(): ProposalItem {
        return ProposalItem.create(
            id = id,
            contractRequestItemId = contractRequestItemId,
            unitPrice = unitPrice,
        )
    }

    companion object {
        fun from(
            proposalId: UUID,
            item: ProposalItem,
        ): ProposalItemJpaEntity {
            return ProposalItemJpaEntity(
                id = item.id,
                proposalId = proposalId,
                contractRequestItemId = item.contractRequestItemId,
                unitPrice = item.unitPrice,
            )
        }
    }
}
