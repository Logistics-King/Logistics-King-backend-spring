package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ContractRepositoryImpl(
    private val contractJpaRepository: ContractJpaRepository,
) : ContractRepository {

    override fun save(contract: Contract): Contract {
        return contractJpaRepository.save(ContractJpaEntity.from(contract)).toDomain()
    }

    override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<Contract> {
        return contractJpaRepository.findAllByVendorIdOrderByCreatedAtDesc(vendorId, pageable)
            .map(ContractJpaEntity::toDomain)
    }

    override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Contract> {
        return contractJpaRepository.findAllByAgencyIdOrderByCreatedAtDesc(agencyId, pageable)
            .map(ContractJpaEntity::toDomain)
    }

    override fun existsByContractRequestId(contractRequestId: UUID): Boolean {
        return contractJpaRepository.existsByContractRequestId(contractRequestId)
    }
}
