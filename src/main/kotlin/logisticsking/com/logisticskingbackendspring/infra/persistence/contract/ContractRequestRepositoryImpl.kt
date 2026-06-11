package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
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

    override fun findByIdAndVendorId(
        id: UUID,
        vendorId: UUID,
    ): ContractRequest? {
        return contractRequestJpaRepository.findByIdAndVendorId(
            id = id,
            vendorId = vendorId,
        )?.toDomain()
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
        return contractRequestJpaRepository.findAllByVendorIdOrderByCreatedAtDesc(vendorId, pageable)
            .map(ContractRequestJpaEntity::toDomain)
    }

    override fun findAllByStatus(status: ContractRequestStatus, pageable: Pageable): Page<ContractRequest> {
        return contractRequestJpaRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable)
            .map(ContractRequestJpaEntity::toDomain)
    }
}
