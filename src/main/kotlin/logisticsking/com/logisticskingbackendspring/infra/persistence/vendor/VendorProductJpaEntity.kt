package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProduct
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.SoftDeletableJpaEntity
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "vendor_products")
class VendorProductJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "vendor_id", columnDefinition = "BINARY(16)", nullable = false)
    val vendorId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    val category: ProductCategory,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "description", length = 255)
    val description: String?,

    @Column(name = "average_price", precision = 15, scale = 2)
    val averagePrice: BigDecimal?,

    @Column(name = "average_weight_gram")
    val averageWeightGram: Int?,

    @Enumerated(EnumType.STRING)
    @Column(name = "box_size", length = 30)
    val boxSize: BoxSize?,

    @Column(name = "destination_postal_code", length = 20)
    val destinationPostalCode: String?,

    @Column(name = "destination_address", nullable = false, length = 255)
    val destinationAddress: String,

    @Column(name = "destination_address_detail", length = 255)
    val destinationAddressDetail: String?,

    @Column(name = "fragile", nullable = false)
    val fragile: Boolean,

    @Column(name = "liquid", nullable = false)
    val liquid: Boolean,

    @Column(name = "fresh_food", nullable = false)
    val freshFood: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(name = "cold_chain_type", nullable = false, length = 30)
    val coldChainType: ColdChainType,
) : SoftDeletableJpaEntity() {

    fun toDomain(): VendorProduct {
        return VendorProduct.restore(
            id = id,
            vendorId = vendorId,
            category = category,
            name = name,
            description = description,
            averagePrice = averagePrice,
            averageWeightGram = averageWeightGram,
            boxSize = boxSize,
            destinationPostalCode = destinationPostalCode,
            destinationAddress = destinationAddress,
            destinationAddressDetail = destinationAddressDetail,
            fragile = fragile,
            liquid = liquid,
            freshFood = freshFood,
            coldChainType = coldChainType,
        )
    }

    companion object {
        fun from(product: VendorProduct): VendorProductJpaEntity {
            return VendorProductJpaEntity(
                id = product.id,
                vendorId = product.vendorId,
                category = product.category,
                name = product.name,
                description = product.description,
                averagePrice = product.averagePrice,
                averageWeightGram = product.averageWeightGram,
                boxSize = product.boxSize,
                destinationPostalCode = product.destinationPostalCode,
                destinationAddress = product.destinationAddress,
                destinationAddressDetail = product.destinationAddressDetail,
                fragile = product.fragile,
                liquid = product.liquid,
                freshFood = product.freshFood,
                coldChainType = product.coldChainType,
            )
        }
    }
}
