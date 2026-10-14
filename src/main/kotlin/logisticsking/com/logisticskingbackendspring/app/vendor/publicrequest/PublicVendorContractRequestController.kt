package logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Vendor", description = "화주 공개 계약 요청 API")
@RestController
@RequestMapping("/api/v1/market/contract-requests")
class PublicVendorContractRequestController(
    private val getPublicVendorContractRequestsUseCase: GetPublicVendorContractRequestsUseCase,
    private val getPublicVendorContractRequestSummaryUseCase: GetPublicVendorContractRequestSummaryUseCase,
) {
    @EndpointAccess(publicAccess = true)
    @GetMapping("/summary")
    fun getSummary(@RequestParam(required = false) region: String?): ApiResponse<PublicVendorContractRequestResponse.Summary> =
        ApiResponse.success(response = PublicVendorContractRequestResponse.Summary.from(getPublicVendorContractRequestSummaryUseCase.getPublicVendorContractRequestSummary(region)))

    @EndpointAccess(publicAccess = true)
    @Operation(summary = "화주 공개 계약 요청 목록 조회")
    @GetMapping
    fun getPublicContractRequests(
        @RequestParam(required = false) region: String?,
        @PageableDefault(size = 6) pageable: Pageable,
    ): ApiResponse<PublicVendorContractRequestResponse.List> = ApiResponse.success(
        response = PublicVendorContractRequestResponse.List.from(
            getPublicVendorContractRequestsUseCase.getPublicVendorContractRequests(region, pageable),
        ),
    )
}
