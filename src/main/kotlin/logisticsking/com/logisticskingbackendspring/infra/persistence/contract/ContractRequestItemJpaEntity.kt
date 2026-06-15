package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestItem
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "contract_request_items")
class ContractRequestItemJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "contract_request_id", columnDefinition = "BINARY(16)", nullable = false)
    val contractRequestId: UUID,

    @Column(name = "product_id", columnDefinition = "BINARY(16)")
    val productId: UUID?,

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false, length = 30)
    val productCategory: ProductCategory,

    @Column(name = "product_name", nullable = false, length = 100)
    val productName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "box_size", nullable = false, length = 30)
    val boxSize: BoxSize,

    @Column(name = "box_quantity", nullable = false)
    val boxQuantity: Int,

    @Column(name = "item_quantity", nullable = false)
    val itemQuantity: Int,

    @Column(name = "average_weight_gram")
    val averageWeightGram: Int?,

    @Column(name = "fragile", nullable = false)
    val fragile: Boolean,

    @Column(name = "liquid", nullable = false)
    val liquid: Boolean,

    @Column(name = "fresh_food", nullable = false)
    val freshFood: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "cold_chain_type", nullable = false, length = 30)
    val coldChainType: ColdChainType,

    @Column(name = "target_unit_price", precision = 15, scale = 2)
    val targetUnitPrice: BigDecimal?,
) : BaseJpaEntity() {

    fun toDomain(): ContractRequestItem {
        return ContractRequestItem.create(
            id = id,
            productId = productId,
            productCategory = productCategory,
            productName = productName,
            boxSize = boxSize,
            boxQuantity = boxQuantity,
            itemQuantity = itemQuantity,
            averageWeightGram = averageWeightGram,
            fragile = fragile,
            liquid = liquid,
            freshFood = freshFood,
            coldChainType = coldChainType,
            targetUnitPrice = targetUnitPrice,
        )
    }

    companion object {
        fun from(
            contractRequestId: UUID,
            item: ContractRequestItem,
        ): ContractRequestItemJpaEntity {
            return ContractRequestItemJpaEntity(
                id = item.id,
                contractRequestId = contractRequestId,
                productId = item.productId,
                productCategory = item.productCategory,
                productName = item.productName,
                boxSize = item.boxSize,
                boxQuantity = item.boxQuantity,
                itemQuantity = item.itemQuantity,
                averageWeightGram = item.averageWeightGram,
                fragile = item.fragile,
                liquid = item.liquid,
                freshFood = item.freshFood,
                coldChainType = item.coldChainType,
                targetUnitPrice = item.targetUnitPrice,
            )
        }
    }
}
