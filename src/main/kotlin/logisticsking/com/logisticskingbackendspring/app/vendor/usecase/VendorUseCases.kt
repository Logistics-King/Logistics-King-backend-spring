package logisticsking.com.logisticskingbackendspring.app.vendor.usecase

import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.CreateVendorProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.command.UpdateVendorProductCommand
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorProductResult
import logisticsking.com.logisticskingbackendspring.app.vendor.result.VendorResult
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

interface CreateVendorProductUseCase {
    fun createProduct(command: CreateVendorProductCommand): VendorProductResult
}

interface GetVendorProductsUseCase {
    fun getProducts(userId: UUID, pageable: Pageable): Page<VendorProductResult>
}

interface UpdateVendorProductUseCase {
    fun updateProduct(command: UpdateVendorProductCommand): VendorProductResult
}
