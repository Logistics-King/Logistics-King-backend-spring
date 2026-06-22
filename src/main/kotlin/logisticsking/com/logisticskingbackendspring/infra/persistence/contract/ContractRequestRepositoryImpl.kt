package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractPartyType
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestRepository
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ContractRequestRepositoryImpl(
    private val contractRequestJpaRepository: ContractRequestJpaRepository,
    private val contractRequestItemJpaRepository: ContractRequestItemJpaRepository,
    private val contractRequestQueryRepository: ContractRequestQueryRepository,
) : ContractRequestRepository {

    override fun save(contractRequest: ContractRequest): ContractRequest {
        val saved = contractRequestJpaRepository.save(ContractRequestJpaEntity.from(contractRequest))
        contractRequestItemJpaRepository.deleteAllByContractRequestId(saved.id)
        contractRequestItemJpaRepository.saveAll(
            contractRequest.items.map { ContractRequestItemJpaEntity.from(saved.id, it) }
        )

        return saved.toDomain(contractRequestItemJpaRepository.findAllByContractRequestIdOrderByCreatedAtAsc(saved.id))
    }

    override fun findById(id: UUID): ContractRequest? {
        return contractRequestJpaRepository.findByIdOrNull(id)?.toDomainWithItems()
    }

    override fun findByIdForUpdate(id: UUID): ContractRequest? {
        return contractRequestQueryRepository.findByIdForUpdate(id)?.toDomainWithItems()
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
        )?.toDomainWithItems()
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
        )?.toDomainWithItems()
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
        )?.toDomainWithItems()
    }

    override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<ContractRequest> {
        return contractRequestJpaRepository.findAllByRequesterTypeAndRequesterIdOrderByCreatedAtDesc(
            requesterType = ContractPartyType.VENDOR,
            requesterId = vendorId,
            pageable = pageable,
        )
            .toDomainPageWithItems()
    }

    override fun findAllByRequester(
        requesterType: ContractPartyType,
        requesterId: UUID,
        condition: ContractRequestSearchCondition,
        pageable: Pageable,
    ): Page<ContractRequest> {
        return contractRequestQueryRepository.findAllByRequester(
            requesterType = requesterType,
            requesterId = requesterId,
            condition = condition,
            pageable = pageable,
        ).toDomainPageWithItems()
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
        ).toDomainPageWithItems()
    }

    override fun findAllByStatus(status: ContractRequestStatus, pageable: Pageable): Page<ContractRequest> {
        return contractRequestJpaRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable)
            .toDomainPageWithItems()
    }

    override fun findOpenVendorOffersForAgency(agencyId: UUID, pageable: Pageable): Page<ContractRequest> {
        return contractRequestQueryRepository.findOpenVendorOffersForAgency(
            agencyId = agencyId,
            pageable = pageable,
        ).toDomainPageWithItems()
    }

    override fun existsActiveByVendorIdAndProductIds(
        vendorId: UUID,
        productIds: Collection<UUID>,
        excludedContractRequestId: UUID?,
    ): Boolean {
        return contractRequestQueryRepository.existsActiveByVendorIdAndProductIds(
            vendorId = vendorId,
            productIds = productIds,
            excludedContractRequestId = excludedContractRequestId,
        )
    }

    private fun ContractRequestJpaEntity.toDomainWithItems(): ContractRequest {
        return toDomain(contractRequestItemJpaRepository.findAllByContractRequestIdOrderByCreatedAtAsc(id))
    }

    private fun Page<ContractRequestJpaEntity>.toDomainPageWithItems(): Page<ContractRequest> {
        val ids = content.map(ContractRequestJpaEntity::id)
        val itemsByRequestId = if (ids.isEmpty()) {
            emptyMap()
        } else {
            contractRequestItemJpaRepository.findAllByContractRequestIdInOrderByCreatedAtAsc(ids)
                .groupBy(ContractRequestItemJpaEntity::contractRequestId)
        }

        return map { it.toDomain(itemsByRequestId[it.id].orEmpty()) }
    }
}
