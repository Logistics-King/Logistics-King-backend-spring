package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEvent
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalNegotiationEventType
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "proposal_negotiation_events",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_proposal_negotiation_events_proposal_sequence",
            columnNames = ["proposal_id", "sequence"],
        ),
    ],
    indexes = [
        Index(name = "idx_proposal_negotiation_events_proposal_id", columnList = "proposal_id"),
        Index(name = "idx_proposal_negotiation_events_status", columnList = "status"),
    ],
)
class ProposalNegotiationEventJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "proposal_id", columnDefinition = "BINARY(16)", nullable = false)
    val proposalId: UUID,

    @Column(name = "sequence", nullable = false)
    val sequence: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false, length = 30)
    val actorType: ContractPartyType,

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    val eventType: ProposalNegotiationEventType,

    @Column(name = "unit_price", precision = 15, scale = 2)
    val unitPrice: BigDecimal?,

    @Column(name = "memo", length = 255)
    val memo: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: ProposalNegotiationEventStatus,
) : BaseJpaEntity() {

    fun toDomain(): ProposalNegotiationEvent {
        return ProposalNegotiationEvent.restore(
            id = id,
            proposalId = proposalId,
            sequence = sequence,
            actorType = actorType,
            eventType = eventType,
            unitPrice = unitPrice,
            memo = memo,
            status = status,
        )
    }

    companion object {
        fun from(event: ProposalNegotiationEvent): ProposalNegotiationEventJpaEntity {
            return ProposalNegotiationEventJpaEntity(
                id = event.id,
                proposalId = event.proposalId,
                sequence = event.sequence,
                actorType = event.actorType,
                eventType = event.eventType,
                unitPrice = event.unitPrice,
                memo = event.memo,
                status = event.status,
            )
        }
    }
}
