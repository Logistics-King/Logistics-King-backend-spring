package logisticsking.com.logisticskingbackendspring.domain.vendor

import java.util.UUID

interface VendorRepository {
    fun save(vendor: Vendor): Vendor
    fun findById(id: UUID): Vendor?
    fun findAllByIds(ids: Collection<UUID>): List<Vendor>
    fun findByUserId(userId: UUID): Vendor?
    fun existsByUserId(userId: UUID): Boolean
}
