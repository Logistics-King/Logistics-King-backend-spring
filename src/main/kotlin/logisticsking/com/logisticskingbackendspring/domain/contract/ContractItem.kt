package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class ContractItem(
    val id: UUID,
    val productId: UUID?,
    val productCategory: ProductCategory,
    val productName: String,
    val boxSize: BoxSize,
    val boxQuantity: Int,
    val itemQuantity: Int,
    val averageWeightGram: Int?,
    val fragile: Boolean,
    val liquid: Boolean,
    val freshFood: Boolean,
    val coldChainType: ColdChainType,
    val unitPrice: BigDecimal,
) {
    companion object {
        fun create(
            id: UUID,
            productId: UUID?,
            productCategory: ProductCategory,
            productName: String,
            boxSize: BoxSize,
            boxQuantity: Int,
            itemQuantity: Int,
            averageWeightGram: Int?,
            fragile: Boolean,
            liquid: Boolean,
            freshFood: Boolean,
            coldChainType: ColdChainType,
            unitPrice: BigDecimal,
        ): ContractItem {
            requireDomain(productName.isNotBlank(), ContractRequestErrorCode.INVALID_PRODUCT_NAME)
            requireDomain(
                boxQuantity >= 0 && itemQuantity >= 0 && (boxQuantity > 0 || itemQuantity > 0),
                ContractRequestErrorCode.INVALID_ITEM_QUANTITY,
            )
            requireDomain(
                averageWeightGram == null || averageWeightGram >= 0,
                ContractRequestErrorCode.INVALID_AVERAGE_WEIGHT,
            )
            requireDomain(unitPrice > BigDecimal.ZERO, ContractErrorCode.INVALID_CONTRACT_ITEM_UNIT_PRICE)

            return ContractItem(
                id = id,
                productId = productId,
                productCategory = productCategory,
                productName = productName.trim(),
                boxSize = boxSize,
                boxQuantity = boxQuantity,
                itemQuantity = itemQuantity,
                averageWeightGram = averageWeightGram,
                fragile = fragile,
                liquid = liquid,
                freshFood = freshFood,
                coldChainType = coldChainType,
                unitPrice = unitPrice,
            )
        }

        fun fromRequestItem(
            id: UUID,
            item: ContractRequestItem,
            unitPrice: BigDecimal,
        ): ContractItem {
            return create(
                id = id,
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
                unitPrice = unitPrice,
            )
        }
    }
}
