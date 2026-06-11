package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.util.UUID

class VendorProduct private constructor(
    val id: UUID,
    val vendorId: UUID,
    val category: ProductCategory,
    val name: String,
    val description: String?,
    val averagePrice: BigDecimal?,
    val averageWeightGram: Int?,
    val boxSize: String?,
    val fragile: Boolean,
    val liquid: Boolean,
    val freshFood: Boolean,
    val coldChainType: ColdChainType,
) {

    fun update(
        category: ProductCategory,
        name: String,
        description: String?,
        averagePrice: BigDecimal?,
        averageWeightGram: Int?,
        boxSize: String?,
        fragile: Boolean,
        liquid: Boolean,
        freshFood: Boolean,
        coldChainType: ColdChainType,
    ): VendorProduct {
        return create(
            id = id,
            vendorId = vendorId,
            category = category,
            name = name,
            description = description,
            averagePrice = averagePrice,
            averageWeightGram = averageWeightGram,
            boxSize = boxSize,
            fragile = fragile,
            liquid = liquid,
            freshFood = freshFood,
            coldChainType = coldChainType,
        )
    }

    companion object {
        fun create(
            id: UUID,
            vendorId: UUID,
            category: ProductCategory,
            name: String,
            description: String?,
            averagePrice: BigDecimal?,
            averageWeightGram: Int?,
            boxSize: String?,
            fragile: Boolean,
            liquid: Boolean,
            freshFood: Boolean,
            coldChainType: ColdChainType,
        ): VendorProduct {
            requireDomain(name.isNotBlank(), VendorErrorCode.INVALID_PRODUCT_NAME)
            requireDomain(
                averagePrice == null || averagePrice >= BigDecimal.ZERO,
                VendorErrorCode.INVALID_AVERAGE_PRICE,
            )
            requireDomain(
                averageWeightGram == null || averageWeightGram >= 0,
                VendorErrorCode.INVALID_AVERAGE_WEIGHT,
            )

            return VendorProduct(
                id = id,
                vendorId = vendorId,
                category = category,
                name = name.trim(),
                description = description?.trim()?.takeIf { it.isNotBlank() },
                averagePrice = averagePrice,
                averageWeightGram = averageWeightGram,
                boxSize = boxSize?.trim()?.takeIf { it.isNotBlank() },
                fragile = fragile,
                liquid = liquid,
                freshFood = freshFood,
                coldChainType = coldChainType,
            )
        }

        fun restore(
            id: UUID,
            vendorId: UUID,
            category: ProductCategory,
            name: String,
            description: String?,
            averagePrice: BigDecimal?,
            averageWeightGram: Int?,
            boxSize: String?,
            fragile: Boolean,
            liquid: Boolean,
            freshFood: Boolean,
            coldChainType: ColdChainType,
        ): VendorProduct {
            return VendorProduct(
                id = id,
                vendorId = vendorId,
                category = category,
                name = name,
                description = description,
                averagePrice = averagePrice,
                averageWeightGram = averageWeightGram,
                boxSize = boxSize,
                fragile = fragile,
                liquid = liquid,
                freshFood = freshFood,
                coldChainType = coldChainType,
            )
        }
    }
}
