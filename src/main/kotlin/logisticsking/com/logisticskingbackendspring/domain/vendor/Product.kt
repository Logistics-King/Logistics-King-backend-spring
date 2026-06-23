package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.util.UUID

class Product private constructor(
    val id: UUID,

    val vendorId: UUID,

    val category: ProductCategory,

    val name: String,

    val description: String?,

    val averagePrice: BigDecimal?,

    val averageWeightGram: Int?,

    val boxSize: BoxSize?,

    val boxQuantity: Int,

    val itemQuantity: Int,

    val destinationPostalCode: String?,

    val destinationAddress: String,

    val destinationAddressDetail: String?,

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
        boxSize: BoxSize?,
        boxQuantity: Int,
        itemQuantity: Int,
        destinationPostalCode: String?,
        destinationAddress: String,
        destinationAddressDetail: String?,
        fragile: Boolean,
        liquid: Boolean,
        freshFood: Boolean,
        coldChainType: ColdChainType,
    ): Product {
        return create(
            id = id,
            vendorId = vendorId,
            category = category,
            name = name,
            description = description,
            averagePrice = averagePrice,
            averageWeightGram = averageWeightGram,
            boxSize = boxSize,
            boxQuantity = boxQuantity,
            itemQuantity = itemQuantity,
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
        fun create(
            id: UUID,
            vendorId: UUID,
            category: ProductCategory,
            name: String,
            description: String?,
            averagePrice: BigDecimal?,
            averageWeightGram: Int?,
            boxSize: BoxSize?,
            boxQuantity: Int,
            itemQuantity: Int,
            destinationPostalCode: String?,
            destinationAddress: String,
            destinationAddressDetail: String?,
            fragile: Boolean,
            liquid: Boolean,
            freshFood: Boolean,
            coldChainType: ColdChainType,
        ): Product {
            requireDomain(name.isNotBlank(), VendorErrorCode.INVALID_PRODUCT_NAME)
            requireDomain(destinationAddress.isNotBlank(), VendorErrorCode.INVALID_DESTINATION_ADDRESS)
            requireDomain(
                averagePrice == null || averagePrice >= BigDecimal.ZERO,
                VendorErrorCode.INVALID_AVERAGE_PRICE,
            )
            requireDomain(
                averageWeightGram == null || averageWeightGram >= 0,
                VendorErrorCode.INVALID_AVERAGE_WEIGHT,
            )
            requireDomain(
                boxQuantity >= 0 && itemQuantity >= 0 && (boxQuantity > 0 || itemQuantity > 0),
                VendorErrorCode.INVALID_PRODUCT_QUANTITY,
            )

            return Product(
                id = id,
                vendorId = vendorId,
                category = category,
                name = name.trim(),
                description = description?.trim()?.takeIf { it.isNotBlank() },
                averagePrice = averagePrice,
                averageWeightGram = averageWeightGram,
                boxSize = boxSize,
                boxQuantity = boxQuantity,
                itemQuantity = itemQuantity,
                destinationPostalCode = destinationPostalCode?.trim()?.takeIf { it.isNotBlank() },
                destinationAddress = destinationAddress.trim(),
                destinationAddressDetail = destinationAddressDetail?.trim()?.takeIf { it.isNotBlank() },
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
            boxSize: BoxSize?,
            boxQuantity: Int,
            itemQuantity: Int,
            destinationPostalCode: String?,
            destinationAddress: String,
            destinationAddressDetail: String?,
            fragile: Boolean,
            liquid: Boolean,
            freshFood: Boolean,
            coldChainType: ColdChainType,
        ): Product {
            return Product(
                id = id,
                vendorId = vendorId,
                category = category,
                name = name,
                description = description,
                averagePrice = averagePrice,
                averageWeightGram = averageWeightGram,
                boxSize = boxSize,
                boxQuantity = boxQuantity,
                itemQuantity = itemQuantity,
                destinationPostalCode = destinationPostalCode,
                destinationAddress = destinationAddress,
                destinationAddressDetail = destinationAddressDetail,
                fragile = fragile,
                liquid = liquid,
                freshFood = freshFood,
                coldChainType = coldChainType,
            )
        }
    }
}
