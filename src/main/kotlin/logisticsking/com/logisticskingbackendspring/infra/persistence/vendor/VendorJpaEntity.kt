package logisticsking.com.logisticskingbackendspring.infra.persistence.vendor

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.util.UUID

@Entity
@Table(name = "vendors")
class VendorJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    val userId: UUID,

    @Column(name = "business_name", nullable = false, length = 100)
    val businessName: String,

    @Column(name = "business_registration_number", length = 30)
    val businessRegistrationNumber: String?,

    @Column(name = "representative_name", nullable = false, length = 50)
    val representativeName: String,

    @Column(name = "phone_number", nullable = false, length = 30)
    val phoneNumber: String,

    @Column(name = "postal_code", length = 20)
    val postalCode: String?,

    @Column(name = "address", nullable = false, length = 255)
    val address: String,

    @Column(name = "address_detail", length = 255)
    val addressDetail: String?,

    @Column(name = "main_region", nullable = false, length = 100)
    val mainRegion: String,
) : BaseJpaEntity() {

    fun toDomain(): Vendor {
        return Vendor.restore(
            id = id,
            userId = userId,
            businessName = businessName,
            businessRegistrationNumber = businessRegistrationNumber,
            representativeName = representativeName,
            phoneNumber = phoneNumber,
            postalCode = postalCode,
            address = address,
            addressDetail = addressDetail,
            mainRegion = mainRegion,
        )
    }

    companion object {
        fun from(vendor: Vendor): VendorJpaEntity {
            return VendorJpaEntity(
                id = vendor.id,
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
