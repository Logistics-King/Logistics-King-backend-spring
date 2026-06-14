package logisticsking.com.logisticskingbackendspring.domain.contract

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ContractRequestRepository {
    fun save(contractRequest: ContractRequest): ContractRequest
    fun findById(id: UUID): ContractRequest?
    fun findByIdForUpdate(id: UUID): ContractRequest?
    fun findByIdAndRequesterForUpdate(
        id: UUID,
        requesterType: ContractPartyType,
        requesterId: UUID,
    ): ContractRequest?
    fun findByIdAndApproverForUpdate(
        id: UUID,
        approverType: ContractPartyType,
        approverId: UUID,
    ): ContractRequest?
    fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): ContractRequest?
    fun findByIdAndVendorIdForUpdate(
        id: UUID,
        vendorId: UUID,
    ): ContractRequest?
    fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<ContractRequest>
    fun findAllByRequester(
        requesterType: ContractPartyType,
        requesterId: UUID,
        pageable: Pageable,
    ): Page<ContractRequest>
    fun findAllByApprover(
        approverType: ContractPartyType,
        approverId: UUID,
        pageable: Pageable,
    ): Page<ContractRequest>
    fun findAllByStatus(status: ContractRequestStatus, pageable: Pageable): Page<ContractRequest>
    fun findOpenVendorOffersForAgency(agencyId: UUID, pageable: Pageable): Page<ContractRequest>
}
