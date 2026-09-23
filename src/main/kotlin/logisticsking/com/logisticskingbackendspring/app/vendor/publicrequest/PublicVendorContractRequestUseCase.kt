package logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPublicVendorContractRequestsUseCase {
    fun getPublicVendorContractRequests(region: String?, pageable: Pageable): Page<PublicVendorContractRequestResult>
}

interface GetPublicVendorContractRequestSummaryUseCase {
    fun getPublicVendorContractRequestSummary(region: String?): PublicVendorContractRequestSummaryResult
}
