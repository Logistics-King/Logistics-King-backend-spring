package logisticsking.com.logisticskingbackendspring.domain.contract

import java.util.UUID

interface ContractRequestRepository {
    fun save(contractRequest: ContractRequest): ContractRequest
    fun findById(id: UUID): ContractRequest?
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): ContractRequest?
    fun findAllByVendorId(vendorId: UUID): List<ContractRequest>
}
