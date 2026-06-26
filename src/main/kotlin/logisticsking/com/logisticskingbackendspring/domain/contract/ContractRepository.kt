package logisticsking.com.logisticskingbackendspring.domain.contract

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface ContractRepository {
    fun save(contract: Contract): Contract
    fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<Contract>
    fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Contract>
    fun findRecentAgencyIdsByVendorId(vendorId: UUID, limit: Int): List<UUID>
    fun existsByContractRequestId(contractRequestId: UUID): Boolean
}
