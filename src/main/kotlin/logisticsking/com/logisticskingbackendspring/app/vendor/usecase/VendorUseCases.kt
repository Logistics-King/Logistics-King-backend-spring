package logisticsking.com.logisticskingbackendspring.app.vendor.usecase

import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.result.ProductResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CreateVendorUseCase {
    fun create(command: CreateVendorCommand): VendorResult
}

interface GetMyVendorUseCase {
    fun getMyVendor(userId: UUID): VendorResult
}

interface UpdateVendorUseCase {
    fun update(command: UpdateVendorCommand): VendorResult
}

interface CreateProductUseCase {
    fun createProduct(command: CreateProductCommand): ProductResult
}

interface GetProductsUseCase {
    fun getProducts(
        userId: UUID,
        condition: ProductSearchCondition,
        pageable: Pageable,
    ): Page<ProductResult>
}

interface UpdateProductUseCase {
    fun updateProduct(command: UpdateProductCommand): ProductResult
}
