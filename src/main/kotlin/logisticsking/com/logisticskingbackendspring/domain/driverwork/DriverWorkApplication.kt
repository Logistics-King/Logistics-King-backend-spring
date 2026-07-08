package logisticsking.com.logisticskingbackendspring.domain.driverwork

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.util.UUID

class DriverWorkApplication private constructor(
    val id: UUID,

    val driverWorkId: UUID,

    val deliverId: UUID,

    val status: DriverWorkApplicationStatus,

    val memo: String?,
) {

    fun withdraw(): DriverWorkApplication {
        return changeStatus(DriverWorkApplicationStatus.WITHDRAWN)
    }

    fun select(): DriverWorkApplication {
        return changeStatus(DriverWorkApplicationStatus.SELECTED)
    }

    fun reject(): DriverWorkApplication {
        return changeStatus(DriverWorkApplicationStatus.REJECTED)
    }

    private fun changeStatus(nextStatus: DriverWorkApplicationStatus): DriverWorkApplication {
        requireDomain(
            status == DriverWorkApplicationStatus.APPLIED,
            DriverWorkErrorCode.ONLY_APPLIED_APPLICATION_CAN_BE_CHANGED,
        )

        return restore(
            id = id,
            driverWorkId = driverWorkId,
            deliverId = deliverId,
            status = nextStatus,
            memo = memo,
        )
    }

    companion object {
        fun create(
            id: UUID,
            driverWorkId: UUID,
            deliverId: UUID,
            memo: String?,
        ): DriverWorkApplication {
            return DriverWorkApplication(
                id = id,
                driverWorkId = driverWorkId,
                deliverId = deliverId,
                status = DriverWorkApplicationStatus.APPLIED,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
            )
        }

        fun restore(
            id: UUID,
            driverWorkId: UUID,
            deliverId: UUID,
            status: DriverWorkApplicationStatus,
            memo: String?,
        ): DriverWorkApplication {
            return DriverWorkApplication(
                id = id,
                driverWorkId = driverWorkId,
                deliverId = deliverId,
                status = status,
                memo = memo,
            )
        }
    }
}
