package logisticsking.com.logisticskingbackendspring.app.vendor

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.vendor.dto.VendorRequest
import logisticsking.com.logisticskingbackendspring.app.vendor.dto.VendorResponse
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.CreateVendorProductUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.CreateVendorUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.GetMyVendorUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.GetVendorProductsUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.UpdateVendorProductUseCase
import logisticsking.com.logisticskingbackendspring.app.vendor.usecase.UpdateVendorUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "Vendor", description = "화주 API")
@SecurityRequirement(name = "accessTokenCookie")
@EndpointAccess(roles = [UserRole.ADMIN, UserRole.VENDOR])
@RestController
@RequestMapping("/api/v1/vendors")
class VendorController(
    private val createVendorUseCase: CreateVendorUseCase,
    private val getMyVendorUseCase: GetMyVendorUseCase,
    private val updateVendorUseCase: UpdateVendorUseCase,
    private val createVendorProductUseCase: CreateVendorProductUseCase,
    private val getVendorProductsUseCase: GetVendorProductsUseCase,
    private val updateVendorProductUseCase: UpdateVendorProductUseCase,
) {

    @Operation(summary = "내 화주 프로필 생성", description = "로그인한 화주 사용자의 사업 프로필을 생성합니다.")
    @PostMapping("/me")
    fun createMyVendor(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: VendorRequest.Create,
    ): ApiResponse<VendorResponse.Detail> {
        val result = createVendorUseCase.create(request.toCommand(user.userId))

        return ApiResponse.success(
            response = VendorResponse.Detail.from(result),
        )
    }

    @Operation(summary = "내 화주 프로필 조회", description = "로그인한 화주 사용자의 사업 프로필을 조회합니다.")
    @GetMapping("/me")
    fun getMyVendor(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<VendorResponse.Detail> {
        val result = getMyVendorUseCase.getMyVendor(user.userId)

        return ApiResponse.success(
            response = VendorResponse.Detail.from(result),
        )
    }

    @Operation(summary = "내 화주 프로필 수정", description = "로그인한 화주 사용자의 사업 프로필을 수정합니다.")
    @PutMapping("/me")
    fun updateMyVendor(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: VendorRequest.Update,
    ): ApiResponse<VendorResponse.Detail> {
        val result = updateVendorUseCase.update(request.toCommand(user.userId))

        return ApiResponse.success(
            response = VendorResponse.Detail.from(result),
        )
    }

    @Operation(summary = "화주 배송 품목 생성", description = "계약 요청과 단가 산정에 사용할 배송 품목 프로필을 생성합니다.")
    @PostMapping("/me/products")
    fun createProduct(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: VendorRequest.CreateProduct,
    ): ApiResponse<VendorResponse.ProductDetail> {
        val result = createVendorProductUseCase.createProduct(request.toCommand(user.userId))

        return ApiResponse.success(
            response = VendorResponse.ProductDetail.from(result),
        )
    }

    @Operation(summary = "화주 배송 품목 목록 조회", description = "로그인한 화주의 배송 품목 프로필 목록을 조회합니다.")
    @GetMapping("/me/products")
    fun getProducts(
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ApiResponse<VendorResponse.ProductList> {
        val results = getVendorProductsUseCase.getProducts(user.userId)

        return ApiResponse.success(
            response = VendorResponse.ProductList(
                products = results.map(VendorResponse.ProductDetail::from),
            )
        )
    }

    @Operation(summary = "화주 배송 품목 수정", description = "로그인한 화주의 배송 품목 프로필을 수정합니다.")
    @PutMapping("/me/products/{productId}")
    fun updateProduct(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable productId: UUID,
        @RequestBody request: VendorRequest.UpdateProduct,
    ): ApiResponse<VendorResponse.ProductDetail> {
        val result = updateVendorProductUseCase.updateProduct(request.toCommand(user.userId, productId))

        return ApiResponse.success(
            response = VendorResponse.ProductDetail.from(result),
        )
    }
}
