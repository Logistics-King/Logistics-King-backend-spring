package logisticsking.com.logisticskingbackendspring.app.vendor.result

import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProduct
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

data class VendorProductResult(
    val productId: UUID,
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
    val requiresColdChain: Boolean,
) {
    companion object {
        fun from(product: VendorProduct): VendorProductResult {
            return VendorProductResult(
                productId = product.id,
                vendorId = product.vendorId,
                category = product.category,
                name = product.name,
                description = product.description,
                averagePrice = product.averagePrice,
                averageWeightGram = product.averageWeightGram,
                boxSize = product.boxSize,
                fragile = product.fragile,
                liquid = product.liquid,
                freshFood = product.freshFood,
                requiresColdChain = product.requiresColdChain,
            )
        }
    }
}
