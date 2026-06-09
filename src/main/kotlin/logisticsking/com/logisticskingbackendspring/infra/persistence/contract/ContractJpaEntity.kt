package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractStatus
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "contracts")
class ContractJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "contract_request_id", columnDefinition = "BINARY(16)", nullable = false)
    val contractRequestId: UUID,

    @Column(name = "proposal_id", columnDefinition = "BINARY(16)", nullable = false)
    val proposalId: UUID,

    @Column(name = "vendor_id", columnDefinition = "BINARY(16)", nullable = false)
    val vendorId: UUID,

    @Column(name = "agency_id", columnDefinition = "BINARY(16)", nullable = false)
    val agencyId: UUID,

    @Column(name = "pickup_region", nullable = false, length = 100)
    val pickupRegion: String,

    @Column(name = "pickup_address", length = 255)
    val pickupAddress: String?,

    @Column(name = "monthly_volume", nullable = false)
    val monthlyVolume: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false, length = 30)
    val productCategory: ProductCategory,

    @Column(name = "product_name", nullable = false, length = 100)
    val productName: String,

    @Column(name = "box_size", nullable = false, length = 30)
    val boxSize: String,

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

    @Column(name = "cold_chain_available", nullable = false)
    val coldChainAvailable: Boolean,

    @Column(name = "memo", length = 255)
    val memo: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: ContractStatus,
) : BaseJpaEntity() {

    fun toDomain(): Contract {
        return Contract.restore(
            id = id,
            contractRequestId = contractRequestId,
            proposalId = proposalId,
            vendorId = vendorId,
            agencyId = agencyId,
            pickupRegion = pickupRegion,
            pickupAddress = pickupAddress,
            monthlyVolume = monthlyVolume,
            productCategory = productCategory,
            productName = productName,
            boxSize = boxSize,
            unitPrice = unitPrice,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryAvailable = saturdayDeliveryAvailable,
            returnAvailable = returnAvailable,
            coldChainAvailable = coldChainAvailable,
            memo = memo,
            status = status,
        )
    }

    companion object {
        fun from(contract: Contract): ContractJpaEntity {
            return ContractJpaEntity(
                id = contract.id,
                contractRequestId = contract.contractRequestId,
                proposalId = contract.proposalId,
                vendorId = contract.vendorId,
                agencyId = contract.agencyId,
                pickupRegion = contract.pickupRegion,
                pickupAddress = contract.pickupAddress,
                monthlyVolume = contract.monthlyVolume,
                productCategory = contract.productCategory,
                productName = contract.productName,
                boxSize = contract.boxSize,
                unitPrice = contract.unitPrice,
                pickupStartTime = contract.pickupStartTime,
                pickupEndTime = contract.pickupEndTime,
                saturdayDeliveryAvailable = contract.saturdayDeliveryAvailable,
                returnAvailable = contract.returnAvailable,
                coldChainAvailable = contract.coldChainAvailable,
                memo = contract.memo,
                status = contract.status,
            )
        }
    }
}
