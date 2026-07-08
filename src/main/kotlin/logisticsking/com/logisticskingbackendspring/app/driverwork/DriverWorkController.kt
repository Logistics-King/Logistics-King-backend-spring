package logisticsking.com.logisticskingbackendspring.app.driverwork

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import logisticsking.com.logisticskingbackendspring.app.common.ApiResponse
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.DriverWorkIdCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.SelectDriverWorkApplicationCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.dto.DriverWorkRequest
import logisticsking.com.logisticskingbackendspring.app.driverwork.dto.DriverWorkResponse
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.ApplyDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.AssignDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.CancelDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.CompleteDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.CreateDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetAgencyDriverWorksUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetAssignedDriverWorksUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetDriverWorkApplicationsUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetMyDriverWorkApplicationsUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetOpenDriverWorksUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.SelectDriverWorkApplicationUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.WithdrawDriverWorkApplicationUseCase
import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkSearchCondition
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkStatus
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.security.AuthenticatedUser
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.UUID

@Tag(name = "DriverWork", description = "배송기사 일감 API")
@SecurityRequirement(name = "accessTokenCookie")
@RestController
@RequestMapping("/api/v1/driver-works")
class DriverWorkController(
    private val createDriverWorkUseCase: CreateDriverWorkUseCase,
    private val getAgencyDriverWorksUseCase: GetAgencyDriverWorksUseCase,
    private val getOpenDriverWorksUseCase: GetOpenDriverWorksUseCase,
    private val getAssignedDriverWorksUseCase: GetAssignedDriverWorksUseCase,
    private val getDriverWorkApplicationsUseCase: GetDriverWorkApplicationsUseCase,
    private val getMyDriverWorkApplicationsUseCase: GetMyDriverWorkApplicationsUseCase,
    private val applyDriverWorkUseCase: ApplyDriverWorkUseCase,
    private val withdrawDriverWorkApplicationUseCase: WithdrawDriverWorkApplicationUseCase,
    private val selectDriverWorkApplicationUseCase: SelectDriverWorkApplicationUseCase,
    private val assignDriverWorkUseCase: AssignDriverWorkUseCase,
    private val cancelDriverWorkUseCase: CancelDriverWorkUseCase,
    private val completeDriverWorkUseCase: CompleteDriverWorkUseCase,
) {

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "배송기사 일감 생성", description = "대리점이 최종 계약 물량에 대한 기사 일감을 생성합니다. assignedDeliverId가 있으면 직접 할당, 없으면 OPEN 일감입니다.")
    @PostMapping
    fun create(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: DriverWorkRequest.Create,
    ): ApiResponse<DriverWorkResponse.Detail> {
        val result = createDriverWorkUseCase.create(request.toCommand(user.userId))

        return ApiResponse.success(
            response = DriverWorkResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "대리점 기사 일감 목록 조회", description = "로그인한 대리점이 생성한 기사 일감 목록을 조회합니다.")
    @GetMapping("/agency/me")
    fun getAgencyDriverWorks(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false) status: DriverWorkStatus?,
        @RequestParam(required = false) serviceRegion: String?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        pickupStartDateFrom: LocalDate?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        pickupStartDateTo: LocalDate?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DriverWorkResponse.List> {
        val results = getAgencyDriverWorksUseCase.getAgencyDriverWorks(
            userId = user.userId,
            condition = DriverWorkSearchCondition(
                status = status,
                serviceRegion = serviceRegion,
                pickupStartDateFrom = pickupStartDateFrom,
                pickupStartDateTo = pickupStartDateTo,
            ),
            pageable = pageable,
        )

        return ApiResponse.success(
            response = DriverWorkResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "내 소속 대리점 OPEN 일감 조회", description = "로그인한 소속 배송기사가 신청 가능한 OPEN 일감 목록을 조회합니다.")
    @GetMapping("/driver/me/open")
    fun getOpenDriverWorks(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false) serviceRegion: String?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        pickupStartDateFrom: LocalDate?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        pickupStartDateTo: LocalDate?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DriverWorkResponse.List> {
        val results = getOpenDriverWorksUseCase.getOpenDriverWorks(
            userId = user.userId,
            condition = DriverWorkSearchCondition(
                serviceRegion = serviceRegion,
                pickupStartDateFrom = pickupStartDateFrom,
                pickupStartDateTo = pickupStartDateTo,
            ),
            pageable = pageable,
        )

        return ApiResponse.success(
            response = DriverWorkResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "내 할당 일감 조회", description = "로그인한 배송기사에게 확정 할당된 기사 일감 목록을 조회합니다.")
    @GetMapping("/driver/me/assigned")
    fun getAssignedDriverWorks(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestParam(required = false) status: DriverWorkStatus?,
        @RequestParam(required = false) serviceRegion: String?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        pickupStartDateFrom: LocalDate?,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        pickupStartDateTo: LocalDate?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DriverWorkResponse.List> {
        val results = getAssignedDriverWorksUseCase.getAssignedDriverWorks(
            userId = user.userId,
            condition = DriverWorkSearchCondition(
                status = status,
                serviceRegion = serviceRegion,
                pickupStartDateFrom = pickupStartDateFrom,
                pickupStartDateTo = pickupStartDateTo,
            ),
            pageable = pageable,
        )

        return ApiResponse.success(
            response = DriverWorkResponse.List.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "내 기사 일감 신청 목록 조회", description = "로그인한 배송기사가 신청한 기사 일감 신청 목록을 조회합니다.")
    @GetMapping("/driver/me/applications")
    fun getMyApplications(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DriverWorkResponse.ApplicationList> {
        val results = getMyDriverWorkApplicationsUseCase.getMyApplications(
            userId = user.userId,
            pageable = pageable,
        )

        return ApiResponse.success(
            response = DriverWorkResponse.ApplicationList.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "기사 일감 신청자 목록 조회", description = "대리점이 특정 기사 일감의 신청자 목록을 조회합니다.")
    @GetMapping("/{driverWorkId}/applications")
    fun getApplications(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable driverWorkId: UUID,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ApiResponse<DriverWorkResponse.ApplicationList> {
        val results = getDriverWorkApplicationsUseCase.getApplications(
            userId = user.userId,
            driverWorkId = driverWorkId,
            pageable = pageable,
        )

        return ApiResponse.success(
            response = DriverWorkResponse.ApplicationList.from(results),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "기사 일감 신청", description = "소속 배송기사가 OPEN 상태 기사 일감에 신청합니다.")
    @PostMapping("/{driverWorkId}/applications")
    fun apply(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable driverWorkId: UUID,
        @RequestBody request: DriverWorkRequest.Apply,
    ): ApiResponse<DriverWorkResponse.Application> {
        val result = applyDriverWorkUseCase.apply(request.toCommand(user.userId, driverWorkId))

        return ApiResponse.success(
            response = DriverWorkResponse.Application.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.DRIVER])
    @Operation(summary = "내 기사 일감 신청 철회", description = "소속 배송기사가 OPEN 상태 일감에 대한 신청을 철회합니다.")
    @DeleteMapping("/{driverWorkId}/applications/me")
    fun withdraw(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable driverWorkId: UUID,
    ): ApiResponse<DriverWorkResponse.Application> {
        val result = withdrawDriverWorkApplicationUseCase.withdraw(
            DriverWorkIdCommand(
                userId = user.userId,
                driverWorkId = driverWorkId,
            )
        )

        return ApiResponse.success(
            response = DriverWorkResponse.Application.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "기사 일감 신청자 선택", description = "대리점이 신청자 중 한 명을 선택해 기사 일감을 확정합니다.")
    @PostMapping("/{driverWorkId}/applications/{applicationId}/select")
    fun select(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable driverWorkId: UUID,
        @PathVariable applicationId: UUID,
    ): ApiResponse<DriverWorkResponse.Detail> {
        val result = selectDriverWorkApplicationUseCase.select(
            SelectDriverWorkApplicationCommand(
                userId = user.userId,
                driverWorkId = driverWorkId,
                applicationId = applicationId,
            )
        )

        return ApiResponse.success(
            response = DriverWorkResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "기사 일감 직접 할당", description = "대리점이 OPEN 상태 기사 일감을 소속 배송기사에게 직접 할당합니다.")
    @PostMapping("/{driverWorkId}/assign")
    fun assign(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable driverWorkId: UUID,
        @RequestBody request: DriverWorkRequest.Assign,
    ): ApiResponse<DriverWorkResponse.Detail> {
        val result = assignDriverWorkUseCase.assign(request.toCommand(user.userId, driverWorkId))

        return ApiResponse.success(
            response = DriverWorkResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "기사 일감 취소", description = "대리점이 완료 전 기사 일감을 취소합니다.")
    @PostMapping("/{driverWorkId}/cancel")
    fun cancel(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable driverWorkId: UUID,
    ): ApiResponse<DriverWorkResponse.Detail> {
        val result = cancelDriverWorkUseCase.cancel(
            DriverWorkIdCommand(
                userId = user.userId,
                driverWorkId = driverWorkId,
            )
        )

        return ApiResponse.success(
            response = DriverWorkResponse.Detail.from(result),
        )
    }

    @EndpointAccess(roles = [UserRole.AGENCY])
    @Operation(summary = "기사 일감 완료", description = "대리점이 할당된 기사 일감을 완료 처리합니다.")
    @PostMapping("/{driverWorkId}/complete")
    fun complete(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable driverWorkId: UUID,
    ): ApiResponse<DriverWorkResponse.Detail> {
        val result = completeDriverWorkUseCase.complete(
            DriverWorkIdCommand(
                userId = user.userId,
                driverWorkId = driverWorkId,
            )
        )

        return ApiResponse.success(
            response = DriverWorkResponse.Detail.from(result),
        )
    }
}
