package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestRepository
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ContractRequestRepositoryImpl(
    private val contractRequestJpaRepository: ContractRequestJpaRepository,
    private val contractRequestQueryRepository: ContractRequestQueryRepository,
) : ContractRequestRepository {

    override fun save(contractRequest: ContractRequest): ContractRequest {
        return contractRequestJpaRepository.save(ContractRequestJpaEntity.from(contractRequest)).toDomain()
    }

    override fun findById(id: UUID): ContractRequest? {
        return contractRequestJpaRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findByIdForUpdate(id: UUID): ContractRequest? {
        return contractRequestQueryRepository.findByIdForUpdate(id)?.toDomain()
    }

    override fun findByIdAndRequesterForUpdate(
        id: UUID,
        requesterType: ContractPartyType,
        requesterId: UUID,
    ): ContractRequest? {
        return contractRequestQueryRepository.findByIdAndRequesterForUpdate(
            id = id,
            requesterType = requesterType,
            requesterId = requesterId,
        )?.toDomain()
    }

    override fun findByIdAndApproverForUpdate(
        id: UUID,
        approverType: ContractPartyType,
        approverId: UUID,
    ): ContractRequest? {
        return contractRequestQueryRepository.findByIdAndApproverForUpdate(
            id = id,
            approverType = approverType,
            approverId = approverId,
        )?.toDomain()
    }

    override fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): ContractRequest? {
        return findById(id)?.takeIf { it.vendorId == vendorId }
    }

    override fun findByIdAndVendorIdForUpdate(
        id: UUID,
        vendorId: UUID,
    ): ContractRequest? {
        return contractRequestQueryRepository.findByIdAndVendorIdForUpdate(
            id = id,
            vendorId = vendorId,
        )?.toDomain()
    }

    override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<ContractRequest> {
        return contractRequestJpaRepository.findAllByRequesterTypeAndRequesterIdOrderByCreatedAtDesc(
            requesterType = ContractPartyType.VENDOR,
            requesterId = vendorId,
            pageable = pageable,
        )
            .map(ContractRequestJpaEntity::toDomain)
    }

    override fun findAllByRequester(
        requesterType: ContractPartyType,
        requesterId: UUID,
        pageable: Pageable,
    ): Page<ContractRequest> {
        return contractRequestJpaRepository.findAllByRequesterTypeAndRequesterIdOrderByCreatedAtDesc(
            requesterType = requesterType,
            requesterId = requesterId,
            pageable = pageable,
        ).map(ContractRequestJpaEntity::toDomain)
    }

    override fun findAllByApprover(
        approverType: ContractPartyType,
        approverId: UUID,
        pageable: Pageable,
    ): Page<ContractRequest> {
        return contractRequestJpaRepository.findAllByApproverTypeAndApproverIdOrderByCreatedAtDesc(
            approverType = approverType,
            approverId = approverId,
            pageable = pageable,
        ).map(ContractRequestJpaEntity::toDomain)
    }

    override fun findAllByStatus(status: ContractRequestStatus, pageable: Pageable): Page<ContractRequest> {
        return contractRequestJpaRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable)
            .map(ContractRequestJpaEntity::toDomain)
    }

    override fun findOpenVendorOffersForAgency(agencyId: UUID, pageable: Pageable): Page<ContractRequest> {
        return contractRequestQueryRepository.findOpenVendorOffersForAgency(
            agencyId = agencyId,
            pageable = pageable,
        ).map(ContractRequestJpaEntity::toDomain)
    }
}
