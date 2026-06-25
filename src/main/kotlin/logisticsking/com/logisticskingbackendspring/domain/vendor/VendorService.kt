package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.result.ProductResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.CreateProductUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.CreateVendorUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.GetMyVendorUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.GetProductsUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.UpdateProductUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.UpdateVendorUseCase
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class VendorService(
    private val userRepository: UserRepository,
    private val vendorRepository: VendorRepository,
    private val productRepository: ProductRepository,
    private val idGenerator: IdGenerator,
) : CreateVendorUseCase,
    GetMyVendorUseCase,
    UpdateVendorUseCase,
    CreateProductUseCase,
    GetProductsUseCase,
    UpdateProductUseCase {

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
    override fun createProduct(command: CreateProductCommand): ProductResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val product = Product.create(
            id = idGenerator.generate(),
            vendorId = vendor.id,
            category = command.category,
            name = command.name,
            description = command.description,
            averagePrice = command.averagePrice,
            averageWeightGram = command.averageWeightGram,
            boxSize = command.boxSize,
            boxQuantity = command.boxQuantity,
            itemQuantity = command.itemQuantity,
            destinationPostalCode = command.destinationPostalCode,
            destinationAddress = command.destinationAddress,
            destinationAddressDetail = command.destinationAddressDetail,
            fragile = command.fragile,
            liquid = command.liquid,
            freshFood = command.freshFood,
            coldChainType = command.coldChainType,
        )

        return ProductResult.from(productRepository.save(product))
    }

    @Transactional(readOnly = true)
    override fun getProducts(
        userId: UUID,
        condition: ProductSearchCondition,
        pageable: Pageable,
    ): Page<ProductResult> {
        findVendorUser(userId)
        val vendor = findVendorByUserId(userId)

        return productRepository.findAllByVendorId(vendor.id, condition, pageable)
            .map(ProductResult::from)
    }

    @Transactional
    override fun updateProduct(command: UpdateProductCommand): ProductResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val product = productRepository.findByIdAndVendorId(
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
            boxQuantity = command.boxQuantity,
            itemQuantity = command.itemQuantity,
            destinationPostalCode = command.destinationPostalCode,
            destinationAddress = command.destinationAddress,
            destinationAddressDetail = command.destinationAddressDetail,
            fragile = command.fragile,
            liquid = command.liquid,
            freshFood = command.freshFood,
            coldChainType = command.coldChainType,
        )

        return ProductResult.from(productRepository.save(updated))
    }

    private fun findVendorUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(VendorErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.VENDOR) {
            throw GlobalException(VendorErrorCode.USER_IS_NOT_VENDOR)
        }

        return user
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(VendorErrorCode.VENDOR_NOT_FOUND)
    }
}
