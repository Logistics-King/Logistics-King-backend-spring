package logisticsking.com.logisticskingbackendspring.app.driverwork.dto

import io.swagger.v3.oas.annotations.media.Schema
import logisticsking.com.logisticskingbackendspring.app.deliver.dto.DeliverResponse
import logisticsking.com.logisticskingbackendspring.app.driverwork.result.DriverWorkApplicationResult
import logisticsking.com.logisticskingbackendspring.app.driverwork.result.DriverWorkResult
import org.springframework.data.domain.Page
import java.math.BigDecimal
import java.time.LocalDate

sealed interface DriverWorkResponse {
    @Schema(name = "DriverWorkDetailResponse")
    data class Detail(
        val driverWorkId: String,
        val agencyId: String,
        val contractId: String,
        val title: String,
        val serviceRegion: String,
        val pickupStartDate: LocalDate,
        val pickupEndDate: LocalDate?,
        val expectedVolume: Int,
        val unitPrice: BigDecimal,
        val assignedDeliverId: String?,
        val status: String,
        val memo: String?,
        val assignedDeliver: DeliverResponse.Detail?,
    ) : DriverWorkResponse {
        companion object {
            fun from(result: DriverWorkResult): Detail {
                return Detail(
                    driverWorkId = result.driverWorkId.toString(),
                    agencyId = result.agencyId.toString(),
                    contractId = result.contractId.toString(),
                    title = result.title,
                    serviceRegion = result.serviceRegion,
                    pickupStartDate = result.pickupStartDate,
                    pickupEndDate = result.pickupEndDate,
                    expectedVolume = result.expectedVolume,
                    unitPrice = result.unitPrice,
                    assignedDeliverId = result.assignedDeliverId?.toString(),
                    status = result.status.name,
                    memo = result.memo,
                    assignedDeliver = result.assignedDeliver?.let(DeliverResponse.Detail::from),
                )
            }
        }
    }

    @Schema(name = "DriverWorkListResponse")
    data class List(
        val contents: kotlin.collections.List<Detail>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int,
    ) : DriverWorkResponse {
        companion object {
            fun from(results: Page<DriverWorkResult>): List {
                return List(
                    contents = results.content.map(Detail::from),
                    page = results.number,
                    size = results.size,
                    totalElements = results.totalElements,
                    totalPages = results.totalPages,
                )
            }
        }
    }

    @Schema(name = "DriverWorkApplicationResponse")
    data class Application(
        val applicationId: String,
        val driverWorkId: String,
        val deliverId: String,
        val status: String,
        val memo: String?,
        val deliver: DeliverResponse.Detail?,
        val driverWork: Detail?,
    ) : DriverWorkResponse {
        companion object {
            fun from(result: DriverWorkApplicationResult): Application {
                return Application(
                    applicationId = result.applicationId.toString(),
                    driverWorkId = result.driverWorkId.toString(),
                    deliverId = result.deliverId.toString(),
                    status = result.status.name,
                    memo = result.memo,
                    deliver = result.deliver?.let(DeliverResponse.Detail::from),
                    driverWork = result.driverWork?.let(Detail::from),
                )
            }
        }
    }

    @Schema(name = "DriverWorkApplicationListResponse")
    data class ApplicationList(
        val contents: kotlin.collections.List<Application>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int,
    ) : DriverWorkResponse {
        companion object {
            fun from(results: Page<DriverWorkApplicationResult>): ApplicationList {
                return ApplicationList(
                    contents = results.content.map(Application::from),
                    page = results.number,
                    size = results.size,
                    totalElements = results.totalElements,
                    totalPages = results.totalPages,
                )
            }
        }
    }
}
