package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.Proposal
import logisticsking.com.logisticskingbackendspring.domain.contract.ProposalStatus
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "proposals",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_proposals_contract_request_agency",
            columnNames = ["contract_request_id", "agency_id"],
        ),
    ],
)
class ProposalJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "contract_request_id", columnDefinition = "BINARY(16)", nullable = false)
    val contractRequestId: UUID,

    @Column(name = "vendor_id", columnDefinition = "BINARY(16)", nullable = false)
    val vendorId: UUID,

    @Column(name = "agency_id", columnDefinition = "BINARY(16)", nullable = false)
    val agencyId: UUID,

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    val unitPrice: BigDecimal,

    @Column(name = "pickup_start_time", nullable = false, length = 10)
    val pickupStartTime: String,

    @Column(name = "pickup_end_time", nullable = false, length = 10)
    val pickupEndTime: String,

    @Column(name = "saturday_delivery_available", nullable = false)
    val saturdayDeliveryAvailable: Boolean,

    @Column(name = "return_available", nullable = false)
    val returnAvailable: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "cold_chain_type", nullable = false, length = 30)
    val coldChainType: ColdChainType,

    @Column(name = "memo", length = 255)
    val memo: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: ProposalStatus,
) : BaseJpaEntity() {

    fun toDomain(): Proposal {
        return Proposal.restore(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = unitPrice,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainType = coldChainType,
            memo = memo,
            status = status,
        )
    }

    companion object {
        fun from(proposal: Proposal): ProposalJpaEntity {
            return ProposalJpaEntity(
                id = proposal.id,
                contractRequestId = proposal.contractRequestId,
                vendorId = proposal.vendorId,
                agencyId = proposal.agencyId,
                unitPrice = proposal.unitPrice,
                pickupStartTime = proposal.pickupStartTime,
                pickupEndTime = proposal.pickupEndTime,
                saturdayDeliveryAvailable = proposal.saturdayDeliveryAvailable,
                returnAvailable = proposal.returnAvailable,
                coldChainType = proposal.coldChainType,
                memo = proposal.memo,
                status = proposal.status,
            )
        }
    }
}
