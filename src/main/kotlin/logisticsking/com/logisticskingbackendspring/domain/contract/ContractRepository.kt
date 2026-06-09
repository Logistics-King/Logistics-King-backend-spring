package logisticsking.com.logisticskingbackendspring.domain.contract

import java.util.UUID

interface ContractRepository {
    fun save(contract: Contract): Contract
    fun findAllByVendorId(vendorId: UUID): List<Contract>
    fun findAllByAgencyId(agencyId: UUID): List<Contract>
    fun existsByContractRequestId(contractRequestId: UUID): Boolean
}
