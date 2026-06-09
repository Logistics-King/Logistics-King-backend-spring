package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.contract.command.CancelContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.CreateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.GetContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.command.UpdateContractRequestCommand
import logisticsking.com.logisticskingbackendspring.app.contract.result.ContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CancelContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.CreateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.UpdateContractRequestUseCase
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorProductRepository
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ContractRequestService(
    private val userRepository: UserRepository,
    private val vendorRepository: VendorRepository,
    private val vendorProductRepository: VendorProductRepository,
    private val contractRequestRepository: ContractRequestRepository,
    private val idGenerator: IdGenerator,
) : CreateContractRequestUseCase,
    GetMyContractRequestsUseCase,
    GetContractRequestUseCase,
    UpdateContractRequestUseCase,
    CancelContractRequestUseCase {

    @Transactional
    override fun create(command: CreateContractRequestCommand): ContractRequestResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        validateProduct(command.productId, vendor.id)

        val contractRequest = ContractRequest.create(
            id = idGenerator.generate(),
            vendorId = vendor.id,
            productId = command.productId,
            pickupRegion = command.pickupRegion,
            pickupAddress = command.pickupAddress,
            monthlyVolume = command.monthlyVolume,
            productCategory = command.productCategory,
            productName = command.productName,
            boxSize = command.boxSize,
            pickupStartTime = command.pickupStartTime,
            pickupEndTime = command.pickupEndTime,
            saturdayDeliveryRequired = command.saturdayDeliveryRequired,
            returnRequired = command.returnRequired,
            coldChainRequired = command.coldChainRequired,
            targetUnitPrice = command.targetUnitPrice,
            memo = command.memo,
        )

        return ContractRequestResult.from(contractRequestRepository.save(contractRequest))
    }

    @Transactional(readOnly = true)
    override fun getMyContractRequests(userId: UUID): List<ContractRequestResult> {
        findVendorUser(userId)
        val vendor = findVendorByUserId(userId)

        return contractRequestRepository.findAllByVendorId(vendor.id)
            .map(ContractRequestResult::from)
    }

    @Transactional(readOnly = true)
    override fun get(command: GetContractRequestCommand): ContractRequestResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)

        return ContractRequestResult.from(findContractRequest(command.contractRequestId, vendor.id))
    }

    @Transactional
    override fun update(command: UpdateContractRequestCommand): ContractRequestResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        validateProduct(command.productId, vendor.id)

        val contractRequest = findContractRequest(command.contractRequestId, vendor.id)
        val updated = contractRequest.update(
            productId = command.productId,
            pickupRegion = command.pickupRegion,
            pickupAddress = command.pickupAddress,
            monthlyVolume = command.monthlyVolume,
            productCategory = command.productCategory,
            productName = command.productName,
            boxSize = command.boxSize,
            pickupStartTime = command.pickupStartTime,
            pickupEndTime = command.pickupEndTime,
            saturdayDeliveryRequired = command.saturdayDeliveryRequired,
            returnRequired = command.returnRequired,
            coldChainRequired = command.coldChainRequired,
            targetUnitPrice = command.targetUnitPrice,
            memo = command.memo,
        )

        return ContractRequestResult.from(contractRequestRepository.save(updated))
    }

    @Transactional
    override fun cancel(command: CancelContractRequestCommand): ContractRequestResult {
        findVendorUser(command.userId)
        val vendor = findVendorByUserId(command.userId)
        val contractRequest = findContractRequest(command.contractRequestId, vendor.id)

        return ContractRequestResult.from(contractRequestRepository.save(contractRequest.cancel()))
    }

    private fun findVendorUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(ContractRequestErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.VENDOR) {
            throw GlobalException(ContractRequestErrorCode.USER_IS_NOT_VENDOR)
        }

        return user
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(ContractRequestErrorCode.VENDOR_NOT_FOUND)
    }

    private fun validateProduct(
        productId: UUID?,
        vendorId: UUID,
    ) {
        if (productId == null) {
            return
        }

        vendorProductRepository.findByIdAndVendorId(productId, vendorId)
            ?: throw GlobalException(ContractRequestErrorCode.PRODUCT_NOT_FOUND)
    }

    private fun findContractRequest(
        contractRequestId: UUID,
        vendorId: UUID,
    ): ContractRequest {
        return contractRequestRepository.findByIdAndVendorId(
            id = contractRequestId,
            vendorId = vendorId,
        ) ?: throw GlobalException(ContractRequestErrorCode.NOT_FOUND)
    }
}
