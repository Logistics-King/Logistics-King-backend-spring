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
    private val contractItemJpaRepository: ContractItemJpaRepository,
) : ContractRepository {

    override fun save(contract: Contract): Contract {
        val saved = contractJpaRepository.save(ContractJpaEntity.from(contract))
        contractItemJpaRepository.deleteAllByContractId(saved.id)
        contractItemJpaRepository.saveAll(
            contract.items.map { ContractItemJpaEntity.from(saved.id, it) }
        )

        return saved.toDomain(contractItemJpaRepository.findAllByContractIdOrderByCreatedAtAsc(saved.id))
    }

    override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<Contract> {
        return contractJpaRepository.findAllByVendorIdOrderByCreatedAtDesc(vendorId, pageable)
            .toDomainPageWithItems()
    }

    override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Contract> {
        return contractJpaRepository.findAllByAgencyIdOrderByCreatedAtDesc(agencyId, pageable)
            .toDomainPageWithItems()
    }

    override fun existsByContractRequestId(contractRequestId: UUID): Boolean {
        return contractJpaRepository.existsByContractRequestId(contractRequestId)
    }

    private fun Page<ContractJpaEntity>.toDomainPageWithItems(): Page<Contract> {
        val ids = content.map(ContractJpaEntity::id)
        val itemsByContractId = if (ids.isEmpty()) {
            emptyMap()
        } else {
            contractItemJpaRepository.findAllByContractIdInOrderByCreatedAtAsc(ids)
                .groupBy(ContractItemJpaEntity::contractId)
        }

        return map { it.toDomain(itemsByContractId[it.id].orEmpty()) }
    }
}
