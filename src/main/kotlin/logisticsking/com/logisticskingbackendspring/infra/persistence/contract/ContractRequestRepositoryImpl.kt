package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequest
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ContractRequestRepositoryImpl(
    private val contractRequestJpaRepository: ContractRequestJpaRepository,
) : ContractRequestRepository {

    override fun save(contractRequest: ContractRequest): ContractRequest {
        return contractRequestJpaRepository.save(ContractRequestJpaEntity.from(contractRequest)).toDomain()
    }

    override fun findById(id: UUID): ContractRequest? {
        return contractRequestJpaRepository.findByIdOrNull(id)?.toDomain()
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

    override fun findAllByVendorId(vendorId: UUID): List<ContractRequest> {
        return contractRequestJpaRepository.findAllByVendorIdOrderByCreatedAtDesc(vendorId)
            .map(ContractRequestJpaEntity::toDomain)
    }
}
