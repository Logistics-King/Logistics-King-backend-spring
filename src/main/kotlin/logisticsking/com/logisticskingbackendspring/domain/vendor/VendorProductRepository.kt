package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface VendorProductRepository {
    fun save(product: VendorProduct): VendorProduct
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): VendorProduct?
    fun findAllByVendorId(
        vendorId: UUID,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProduct>

    fun findAll(
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProduct>

    fun findNearbyForAgency(
        agency: Agency,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProduct>
}
