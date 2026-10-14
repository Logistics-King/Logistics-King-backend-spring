package logisticsking.com.logisticskingbackendspring.app.contract.publiccontract

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Contract", description = "공개 계약 조회 API")
@RestController
@RequestMapping("/api/v1/market/contracts")
class RecentPublicContractController(private val getRecentPublicContractsUseCase: GetRecentPublicContractsUseCase) {
    @EndpointAccess(publicAccess = true)
    @Operation(summary = "최근 성사 계약 조회")
    @GetMapping("/recent")
    fun getRecentPublicContracts(@RequestParam(required = false) region: String?): ApiResponse<RecentPublicContractResponse> =
        ApiResponse.success(response = RecentPublicContractResponse.from(getRecentPublicContractsUseCase.getRecentPublicContracts(region)))
}
