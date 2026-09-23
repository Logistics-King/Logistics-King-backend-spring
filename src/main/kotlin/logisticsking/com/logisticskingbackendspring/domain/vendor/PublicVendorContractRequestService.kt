package logisticsking.com.logisticskingbackendspring.domain.vendor

import logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest.GetPublicVendorContractRequestsUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest.GetPublicVendorContractRequestSummaryUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest.PublicVendorContractRequestResult
import logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest.PublicVendorContractRequestSummaryResult
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestRepository
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRequestSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDateTime

@Service
class PublicVendorContractRequestService(private val contractRequestRepository: ContractRequestRepository, private val clock: Clock) : GetPublicVendorContractRequestsUseCase, GetPublicVendorContractRequestSummaryUseCase {
    override fun getPublicVendorContractRequests(region: String?, pageable: Pageable): Page<PublicVendorContractRequestResult> =
        contractRequestRepository.findOpenVendorOffers(ContractRequestSearchCondition(null, null, null, null, null, region?.trim()?.takeIf(String::isNotBlank), null, null), pageable).map(PublicVendorContractRequestResult::from)

    override fun getPublicVendorContractRequestSummary(region: String?): PublicVendorContractRequestSummaryResult {
        val normalizedRegion = region?.trim()?.takeIf(String::isNotBlank)
        val open = contractRequestRepository.findOpenVendorOffers(ContractRequestSearchCondition(null, null, null, null, null, normalizedRegion, null, null), org.springframework.data.domain.PageRequest.of(0, 1)).totalElements
        return PublicVendorContractRequestSummaryResult(open, contractRequestRepository.countOpenVendorOffersCreatedSince(normalizedRegion, LocalDateTime.now(clock).minusDays(7)))
    }
}
