package logisticsking.com.logisticskingbackendspring.app.contract

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.contract.dto.ContractResponse
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyAgencyContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.contract.usecase.GetMyVendorContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Contract", description = "최종 계약 API")
@SecurityRequirement(name = "accessTokenCookie")
@RestController
@RequestMapping("/api/v1/contracts")
class ContractController(
    private val getMyVendorContractsUseCase: GetMyVendorContractsUseCase,
    private val getMyAgencyContractsUseCase: GetMyAgencyContractsUseCase,
) {

    @EndpointAccess(roles = [UserRole.VENDOR])
    @Operation(summary = "화주 최종 계약 목록 조회", description = "로그인한 화주의 확정 계약 목록을 조회합니다.")
    @GetMapping("/vendor/me")
    fun getMyVendorContracts(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<ContractResponse.List> {
        val results = getMyVendorContractsUseCase.getMyVendorContracts(user.userId)

        return ApiResponse.success(
            response = ContractResponse.List(
                contracts = results.map(ContractResponse.Detail::from),
            )
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "대리점 최종 계약 목록 조회", description = "로그인한 대리점이 따낸 확정 계약 목록을 조회합니다.")
    @GetMapping("/agency/me")
    fun getMyAgencyContracts(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<ContractResponse.List> {
        val results = getMyAgencyContractsUseCase.getMyAgencyContracts(user.userId)

        return ApiResponse.success(
            response = ContractResponse.List(
                contracts = results.map(ContractResponse.Detail::from),
            )
        )
    }
}
