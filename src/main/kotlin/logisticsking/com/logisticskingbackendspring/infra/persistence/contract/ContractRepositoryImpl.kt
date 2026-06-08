package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ContractRepositoryImpl(
    private val contractJpaRepository: ContractJpaRepository,
) : ContractRepository {

    override fun save(contract: Contract): Contract {
        return contractJpaRepository.save(ContractJpaEntity.from(contract)).toDomain()
    }

    override fun findAllByVendorId(vendorId: UUID): List<Contract> {
        return contractJpaRepository.findAllByVendorIdOrderByCreatedAtDesc(vendorId)
            .map(ContractJpaEntity::toDomain)
    }

    override fun findAllByAgencyId(agencyId: UUID): List<Contract> {
        return contractJpaRepository.findAllByAgencyIdOrderByCreatedAtDesc(agencyId)
            .map(ContractJpaEntity::toDomain)
    }

    override fun existsByContractRequestId(contractRequestId: UUID): Boolean {
        return contractJpaRepository.existsByContractRequestId(contractRequestId)
    }
}
