package logisticsking.com.logisticskingbackendspring.app.vendor.command

import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class CreateVendorCommand(
    val userId: UUID,
    val businessName: String,
    val businessRegistrationNumber: String?,
    val representativeName: String,
    val phoneNumber: String,
    val postalCode: String?,
    val address: String,
    val addressDetail: String?,
    val mainRegion: String,
)

data class UpdateVendorCommand(
    val userId: UUID,
    val businessName: String,
    val businessRegistrationNumber: String?,
    val representativeName: String,
    val phoneNumber: String,
    val postalCode: String?,
    val address: String,
    val addressDetail: String?,
    val mainRegion: String,
)

data class CreateVendorProductCommand(
    val userId: UUID,
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
)

data class UpdateVendorProductCommand(
    val userId: UUID,
    val productId: UUID,
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
)
