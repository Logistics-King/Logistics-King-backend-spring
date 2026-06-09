package logisticsking.com.logisticskingbackendspring.domain.delivercontract

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class DeliverContract private constructor(
    val id: UUID,
    val agencyId: UUID,
    val deliverId: UUID,
    val serviceRegion: String,
    val expectedMonthlyVolume: Int,
    val unitPrice: BigDecimal,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val memo: String?,
    val status: DeliverContractStatus,
) {

    fun update(
        serviceRegion: String,
        expectedMonthlyVolume: Int,
        unitPrice: BigDecimal,
        startDate: LocalDate,
        endDate: LocalDate?,
        memo: String?,
    ): DeliverContract {
        requireDomain(
            status == DeliverContractStatus.REQUESTED,
            DeliverContractErrorCode.ONLY_REQUESTED_CONTRACT_CAN_BE_UPDATED,
        )

        return create(
            id = id,
            agencyId = agencyId,
            deliverId = deliverId,
            serviceRegion = serviceRegion,
            expectedMonthlyVolume = expectedMonthlyVolume,
            unitPrice = unitPrice,
            startDate = startDate,
            endDate = endDate,
            memo = memo,
            status = status,
        )
    }

    fun accept(): DeliverContract {
        requireDomain(
            status == DeliverContractStatus.REQUESTED,
            DeliverContractErrorCode.ONLY_REQUESTED_CONTRACT_CAN_BE_ACCEPTED,
        )

        return changeStatus(DeliverContractStatus.ACCEPTED)
    }

    fun reject(): DeliverContract {
        requireDomain(
            status == DeliverContractStatus.REQUESTED,
            DeliverContractErrorCode.ONLY_REQUESTED_CONTRACT_CAN_BE_REJECTED,
        )

        return changeStatus(DeliverContractStatus.REJECTED)
    }

    fun cancel(): DeliverContract {
        requireDomain(
            status == DeliverContractStatus.REQUESTED,
            DeliverContractErrorCode.ONLY_REQUESTED_CONTRACT_CAN_BE_CANCELLED,
        )

        return changeStatus(DeliverContractStatus.CANCELLED)
    }

    private fun changeStatus(status: DeliverContractStatus): DeliverContract {
        return restore(
            id = id,
            agencyId = agencyId,
            deliverId = deliverId,
            serviceRegion = serviceRegion,
            expectedMonthlyVolume = expectedMonthlyVolume,
            unitPrice = unitPrice,
            startDate = startDate,
            endDate = endDate,
            memo = memo,
            status = status,
        )
    }

    companion object {
        fun create(
            id: UUID,
            agencyId: UUID,
            deliverId: UUID,
            serviceRegion: String,
            expectedMonthlyVolume: Int,
            unitPrice: BigDecimal,
            startDate: LocalDate,
            endDate: LocalDate?,
            memo: String?,
            status: DeliverContractStatus = DeliverContractStatus.REQUESTED,
        ): DeliverContract {
            requireDomain(serviceRegion.isNotBlank(), DeliverContractErrorCode.INVALID_SERVICE_REGION)
            requireDomain(
                expectedMonthlyVolume > 0,
                DeliverContractErrorCode.INVALID_EXPECTED_MONTHLY_VOLUME,
            )
            requireDomain(unitPrice > BigDecimal.ZERO, DeliverContractErrorCode.INVALID_UNIT_PRICE)
            requireDomain(
                endDate == null || !endDate.isBefore(startDate),
                DeliverContractErrorCode.INVALID_DATE_RANGE,
            )

            return DeliverContract(
                id = id,
                agencyId = agencyId,
                deliverId = deliverId,
                serviceRegion = serviceRegion.trim(),
                expectedMonthlyVolume = expectedMonthlyVolume,
                unitPrice = unitPrice,
                startDate = startDate,
                endDate = endDate,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                status = status,
            )
        }

        fun restore(
            id: UUID,
            agencyId: UUID,
            deliverId: UUID,
            serviceRegion: String,
            expectedMonthlyVolume: Int,
            unitPrice: BigDecimal,
            startDate: LocalDate,
            endDate: LocalDate?,
            memo: String?,
            status: DeliverContractStatus,
        ): DeliverContract {
            return DeliverContract(
                id = id,
                agencyId = agencyId,
                deliverId = deliverId,
                serviceRegion = serviceRegion,
                expectedMonthlyVolume = expectedMonthlyVolume,
                unitPrice = unitPrice,
                startDate = startDate,
                endDate = endDate,
                memo = memo,
                status = status,
            )
        }
    }
}
