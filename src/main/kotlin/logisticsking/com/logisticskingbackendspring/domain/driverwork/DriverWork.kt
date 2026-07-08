package logisticsking.com.logisticskingbackendspring.domain.driverwork

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class DriverWork private constructor(
    val id: UUID,

    val agencyId: UUID,

    val contractId: UUID,

    val title: String,

    val serviceRegion: String,

    val pickupStartDate: LocalDate,

    val pickupEndDate: LocalDate?,

    val expectedVolume: Int,

    val unitPrice: BigDecimal,

    val assignedDeliverId: UUID?,

    val status: DriverWorkStatus,

    val memo: String?,
) {

    fun assign(deliverId: UUID): DriverWork {
        requireDomain(
            status == DriverWorkStatus.OPEN,
            DriverWorkErrorCode.ONLY_OPEN_WORK_CAN_BE_ASSIGNED,
        )

        return restore(
            id = id,
            agencyId = agencyId,
            contractId = contractId,
            title = title,
            serviceRegion = serviceRegion,
            pickupStartDate = pickupStartDate,
            pickupEndDate = pickupEndDate,
            expectedVolume = expectedVolume,
            unitPrice = unitPrice,
            assignedDeliverId = deliverId,
            status = DriverWorkStatus.ASSIGNED,
            memo = memo,
        )
    }

    fun cancel(): DriverWork {
        requireDomain(
            status != DriverWorkStatus.COMPLETED,
            DriverWorkErrorCode.COMPLETED_WORK_CANNOT_BE_CANCELLED,
        )

        return changeStatus(DriverWorkStatus.CANCELLED)
    }

    fun complete(): DriverWork {
        requireDomain(
            status == DriverWorkStatus.ASSIGNED,
            DriverWorkErrorCode.ONLY_ASSIGNED_WORK_CAN_BE_COMPLETED,
        )

        return changeStatus(DriverWorkStatus.COMPLETED)
    }

    private fun changeStatus(nextStatus: DriverWorkStatus): DriverWork {
        return restore(
            id = id,
            agencyId = agencyId,
            contractId = contractId,
            title = title,
            serviceRegion = serviceRegion,
            pickupStartDate = pickupStartDate,
            pickupEndDate = pickupEndDate,
            expectedVolume = expectedVolume,
            unitPrice = unitPrice,
            assignedDeliverId = assignedDeliverId,
            status = nextStatus,
            memo = memo,
        )
    }

    companion object {
        fun create(
            id: UUID,
            agencyId: UUID,
            contractId: UUID,
            title: String,
            serviceRegion: String,
            pickupStartDate: LocalDate,
            pickupEndDate: LocalDate?,
            expectedVolume: Int,
            unitPrice: BigDecimal,
            assignedDeliverId: UUID?,
            memo: String?,
        ): DriverWork {
            requireDomain(title.isNotBlank(), DriverWorkErrorCode.INVALID_TITLE)
            requireDomain(serviceRegion.isNotBlank(), DriverWorkErrorCode.INVALID_SERVICE_REGION)
            requireDomain(expectedVolume > 0, DriverWorkErrorCode.INVALID_EXPECTED_VOLUME)
            requireDomain(unitPrice > BigDecimal.ZERO, DriverWorkErrorCode.INVALID_UNIT_PRICE)
            requireDomain(
                pickupEndDate == null || !pickupEndDate.isBefore(pickupStartDate),
                DriverWorkErrorCode.INVALID_DATE_RANGE,
            )

            return DriverWork(
                id = id,
                agencyId = agencyId,
                contractId = contractId,
                title = title.trim(),
                serviceRegion = serviceRegion.trim(),
                pickupStartDate = pickupStartDate,
                pickupEndDate = pickupEndDate,
                expectedVolume = expectedVolume,
                unitPrice = unitPrice,
                assignedDeliverId = assignedDeliverId,
                status = if (assignedDeliverId == null) DriverWorkStatus.OPEN else DriverWorkStatus.ASSIGNED,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
            )
        }

        fun restore(
            id: UUID,
            agencyId: UUID,
            contractId: UUID,
            title: String,
            serviceRegion: String,
            pickupStartDate: LocalDate,
            pickupEndDate: LocalDate?,
            expectedVolume: Int,
            unitPrice: BigDecimal,
            assignedDeliverId: UUID?,
            status: DriverWorkStatus,
            memo: String?,
        ): DriverWork {
            return DriverWork(
                id = id,
                agencyId = agencyId,
                contractId = contractId,
                title = title,
                serviceRegion = serviceRegion,
                pickupStartDate = pickupStartDate,
                pickupEndDate = pickupEndDate,
                expectedVolume = expectedVolume,
                unitPrice = unitPrice,
                assignedDeliverId = assignedDeliverId,
                status = status,
                memo = memo,
            )
        }
    }
}
