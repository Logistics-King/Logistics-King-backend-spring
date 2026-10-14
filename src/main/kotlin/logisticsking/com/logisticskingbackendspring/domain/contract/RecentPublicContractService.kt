package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.app.contract.publiccontract.GetRecentPublicContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.publiccontract.RecentPublicContractResult
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class RecentPublicContractService(private val contractRepository: ContractRepository, private val clock: Clock) : GetRecentPublicContractsUseCase {
    override fun getRecentPublicContracts(region: String?): List<RecentPublicContractResult> =
        contractRepository.findRecentPublicContracts(region?.trim()?.takeIf(String::isNotBlank), LocalDateTime.now(clock).minusHours(72), 6).map(RecentPublicContractResult::from)
}
