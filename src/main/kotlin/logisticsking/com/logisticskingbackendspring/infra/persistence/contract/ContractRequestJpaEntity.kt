package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "contract_requests")
class ContractRequestJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    val type: ContractRequestType,

    @Enumerated(EnumType.STRING)
    @Column(name = "requester_type", nullable = false, length = 30)
    val requesterType: ContractPartyType,

    @Column(name = "requester_id", columnDefinition = "BINARY(16)", nullable = false)
    val requesterId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "approver_type", nullable = false, length = 30)
    val approverType: ContractPartyType,

    @Column(name = "approver_id", columnDefinition = "BINARY(16)")
    val approverId: UUID?,

    @Column(name = "product_id", columnDefinition = "BINARY(16)")
    val productId: UUID?,

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

    @Enumerated(EnumType.STRING)
    @Column(name = "box_size", nullable = false, length = 30)
    val boxSize: BoxSize,

    @Column(name = "pickup_start_time", nullable = false, length = 10)
    val pickupStartTime: String,

    @Column(name = "pickup_end_time", nullable = false, length = 10)
    val pickupEndTime: String,

    @Column(name = "saturday_delivery_required", nullable = false)
    val saturdayDeliveryRequired: Boolean,

    @Column(name = "return_required", nullable = false)
    val returnRequired: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "cold_chain_type", nullable = false, length = 30)
    val coldChainType: ColdChainType,

    @Column(name = "target_unit_price", precision = 15, scale = 2)
    val targetUnitPrice: BigDecimal?,

    @Column(name = "memo", length = 255)
    val memo: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    val status: ContractRequestStatus,
) : BaseJpaEntity() {

    fun toDomain(): ContractRequest {
        return ContractRequest.restore(
            id = id,
            type = type,
            requesterType = requesterType,
            requesterId = requesterId,
            approverType = approverType,
            approverId = approverId,
            productId = productId,
            pickupRegion = pickupRegion,
            pickupAddress = pickupAddress,
            monthlyVolume = monthlyVolume,
            productCategory = productCategory,
            productName = productName,
            boxSize = boxSize,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryRequired = saturdayDeliveryRequired,
            returnRequired = returnRequired,
            coldChainType = coldChainType,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
            status = status,
        )
    }

    companion object {
        fun from(contractRequest: ContractRequest): ContractRequestJpaEntity {
            return ContractRequestJpaEntity(
                id = contractRequest.id,
                type = contractRequest.type,
                requesterType = contractRequest.requesterType,
                requesterId = contractRequest.requesterId,
                approverType = contractRequest.approverType,
                approverId = contractRequest.approverId,
                productId = contractRequest.productId,
                pickupRegion = contractRequest.pickupRegion,
                pickupAddress = contractRequest.pickupAddress,
                monthlyVolume = contractRequest.monthlyVolume,
                productCategory = contractRequest.productCategory,
                productName = contractRequest.productName,
                boxSize = contractRequest.boxSize,
                pickupStartTime = contractRequest.pickupStartTime,
                pickupEndTime = contractRequest.pickupEndTime,
                saturdayDeliveryRequired = contractRequest.saturdayDeliveryRequired,
                returnRequired = contractRequest.returnRequired,
                coldChainType = contractRequest.coldChainType,
                targetUnitPrice = contractRequest.targetUnitPrice,
                memo = contractRequest.memo,
                status = contractRequest.status,
            )
        }
    }
}
