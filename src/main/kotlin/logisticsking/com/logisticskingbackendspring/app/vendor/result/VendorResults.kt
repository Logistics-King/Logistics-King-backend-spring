package logisticsking.com.logisticskingbackendspring.app.vendor.result

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.Product
import java.math.BigDecimal
import java.util.UUID

data class VendorResult(
    val vendorId: UUID,
    val userId: UUID,
    val businessName: String,
    val businessRegistrationNumber: String?,
    val representativeName: String,
    val phoneNumber: String,
    val postalCode: String?,
    val address: String,
    val addressDetail: String?,
    val mainRegion: String,
) {
    companion object {
        fun from(vendor: Vendor): VendorResult {
            return VendorResult(
                vendorId = vendor.id,
                userId = vendor.userId,
                businessName = vendor.businessName,
                businessRegistrationNumber = vendor.businessRegistrationNumber,
                representativeName = vendor.representativeName,
                phoneNumber = vendor.phoneNumber,
                postalCode = vendor.postalCode,
                address = vendor.address,
                addressDetail = vendor.addressDetail,
                mainRegion = vendor.mainRegion,
            )
        }
    }
}

data class ProductResult(
    val productId: UUID,
    val vendorId: UUID,
    val vendor: VendorResult?,
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
    companion object {
        fun from(product: Product): ProductResult {
            return ProductResult(
                productId = product.id,
                vendorId = product.vendorId,
                vendor = null,
                category = product.category,
                name = product.name,
                description = product.description,
                averagePrice = product.averagePrice,
                averageWeightGram = product.averageWeightGram,
                boxSize = product.boxSize,
                boxQuantity = product.boxQuantity,
                itemQuantity = product.itemQuantity,
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
