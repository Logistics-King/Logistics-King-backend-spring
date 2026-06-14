package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorProductResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.CreateVendorProductUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.CreateVendorUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.GetMyVendorUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.GetVendorProductsUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.UpdateVendorProductUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.UpdateVendorUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.common.ListViewScope
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class VendorService(
    private val userRepository: UserRepository,
    private val agencyRepository: AgencyRepository,
    private val vendorRepository: VendorRepository,
    private val vendorProductRepository: VendorProductRepository,
    private val idGenerator: IdGenerator,
    @Value("\${vendor.product.agency-public-read-enabled:true}") private val agencyPublicReadEnabled: Boolean,
) : CreateVendorUseCase,
    GetMyVendorUseCase,
    UpdateVendorUseCase,
    CreateVendorProductUseCase,
    GetVendorProductsUseCase,
    UpdateVendorProductUseCase {

    @Transactional
    override fun create(command: CreateVendorCommand): VendorResult {
        val user = findVendorUser(command.userId)
        if (vendorRepository.existsByUserId(user.id)) {
            throw GlobalException(VendorErrorCode.VENDOR_ALREADY_EXISTS)
        }

        val vendor = Vendor.create(
            id = idGenerator.generate(),
            userId = user.id,
            businessName = command.businessName,
            businessRegistrationNumber = command.businessRegistrationNumber,
            representativeName = command.representativeName,
            phoneNumber = command.phoneNumber,
            postalCode = command.postalCode,
            address = command.address,
            addressDetail = command.addressDetail,
            mainRegion = command.mainRegion,
        )

        return VendorResult.from(vendorRepository.save(vendor))
    }

    @Transactional(readOnly = true)
    override fun getMyVendor(userId: UUID): VendorResult {
        findVendorUser(userId)

        return VendorResult.from(findVendorByUserId(userId))
    }

    @Transactional
    override fun update(command: UpdateVendorCommand): VendorResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val updated = vendor.update(
            businessName = command.businessName,
            businessRegistrationNumber = command.businessRegistrationNumber,
            representativeName = command.representativeName,
            phoneNumber = command.phoneNumber,
            postalCode = command.postalCode,
            address = command.address,
            addressDetail = command.addressDetail,
            mainRegion = command.mainRegion,
        )

        return VendorResult.from(vendorRepository.save(updated))
    }

    @Transactional
    override fun createProduct(command: CreateVendorProductCommand): VendorProductResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val product = VendorProduct.create(
            id = idGenerator.generate(),
            vendorId = vendor.id,
            category = command.category,
            name = command.name,
            description = command.description,
            averagePrice = command.averagePrice,
            averageWeightGram = command.averageWeightGram,
            boxSize = command.boxSize,
            destinationPostalCode = command.destinationPostalCode,
            destinationAddress = command.destinationAddress,
            destinationAddressDetail = command.destinationAddressDetail,
            fragile = command.fragile,
            liquid = command.liquid,
            freshFood = command.freshFood,
            coldChainType = command.coldChainType,
        )

        return VendorProductResult.from(vendorProductRepository.save(product))
    }

    @Transactional(readOnly = true)
    override fun getProducts(
        userId: UUID,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProductResult> {
        findVendorUser(userId)
        val vendor = findVendorByUserId(userId)

        return vendorProductRepository.findAllByVendorId(vendor.id, condition, pageable)
            .map(VendorProductResult::from)
    }

    @Transactional(readOnly = true)
    override fun getProductsByVendorIdForAgency(
        userId: UUID,
        vendorId: UUID,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProductResult> {
        if (!agencyPublicReadEnabled) {
            throw GlobalException(VendorErrorCode.AGENCY_PRODUCT_PUBLIC_READ_DISABLED)
        }
        findAgencyUser(userId)
        vendorRepository.findById(vendorId)
            ?: throw GlobalException(VendorErrorCode.VENDOR_NOT_FOUND)

        return vendorProductRepository.findAllByVendorId(vendorId, condition, pageable)
            .map(VendorProductResult::from)
    }

    @Transactional(readOnly = true)
    override fun getPublicProductsForAgency(
        userId: UUID,
        condition: VendorProductSearchCondition,
        pageable: Pageable,
    ): Page<VendorProductResult> {
        if (!agencyPublicReadEnabled) {
            throw GlobalException(VendorErrorCode.AGENCY_PRODUCT_PUBLIC_READ_DISABLED)
        }
        val agencyUser = findAgencyUser(userId)

        val products = when (condition.scope) {
            ListViewScope.ALL -> vendorProductRepository.findAll(condition, pageable)
            ListViewScope.NEARBY -> vendorProductRepository.findNearbyForAgency(
                agency = findAgencyByUserId(agencyUser.id),
                condition = condition,
                pageable = pageable,
            )
        }

        return products
            .map(VendorProductResult::from)
    }

    @Transactional
    override fun updateProduct(command: UpdateVendorProductCommand): VendorProductResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val product = vendorProductRepository.findByIdAndVendorId(
            id = command.productId,
            vendorId = vendor.id,
        ) ?: throw GlobalException(VendorErrorCode.PRODUCT_NOT_FOUND)
        val updated = product.update(
            category = command.category,
            name = command.name,
            description = command.description,
            averagePrice = command.averagePrice,
            averageWeightGram = command.averageWeightGram,
            boxSize = command.boxSize,
            destinationPostalCode = command.destinationPostalCode,
            destinationAddress = command.destinationAddress,
            destinationAddressDetail = command.destinationAddressDetail,
            fragile = command.fragile,
            liquid = command.liquid,
            freshFood = command.freshFood,
            coldChainType = command.coldChainType,
        )

        return VendorProductResult.from(vendorProductRepository.save(updated))
    }

    private fun findVendorUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(VendorErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.VENDOR) {
            throw GlobalException(VendorErrorCode.USER_IS_NOT_VENDOR)
        }

        return user
    }

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(VendorErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(VendorErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(VendorErrorCode.VENDOR_NOT_FOUND)
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(VendorErrorCode.AGENCY_NOT_FOUND)
    }
}
