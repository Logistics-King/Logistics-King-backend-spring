package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class VendorRepositoryImpl(
    private val jpaRepository: VendorJpaRepository,
) : VendorRepository {

    override fun save(vendor: Vendor): Vendor {
        return jpaRepository.save(VendorJpaEntity.from(vendor)).toDomain()
    }

    override fun findById(id: UUID): Vendor? {
        return jpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findByUserId(userId: UUID): Vendor? {
        return jpaRepository.findByUserId(userId)?.toDomain()
    }

    override fun existsByUserId(userId: UUID): Boolean {
        return jpaRepository.existsByUserId(userId)
    }
}
