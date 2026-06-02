package logisticsking.com.logisticskingbackendspring.domain.vendor

import java.util.UUID

interface VendorProductRepository {
    fun save(product: VendorProduct): VendorProduct
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): VendorProduct?
    fun findAllByVendorId(vendorId: UUID): List<VendorProduct>
}
