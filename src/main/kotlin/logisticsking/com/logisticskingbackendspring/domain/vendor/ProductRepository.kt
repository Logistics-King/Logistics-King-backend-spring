package logisticsking.com.logisticskingbackendspring.domain.vendor

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ProductRepository {
    fun save(product: Product): Product
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): Product?
    fun findAllByVendorId(
        vendorId: UUID,
        condition: ProductSearchCondition,
        pageable: Pageable,
    ): Page<Product>
    fun findAllByIdsAndVendorIdForUpdate(
        ids: Collection<UUID>,
        vendorId: UUID,
    ): List<Product>
}
