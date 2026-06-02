package logisticsking.com.logisticskingbackendspring.app.deliver.usecase

import logisticsking.com.logisticskingbackendspring.app.deliver.command.CreateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.command.UpdateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.result.DeliverResult
import java.util.UUID

interface CreateDeliverUseCase {
    fun create(command: CreateDeliverCommand): DeliverResult
}

interface GetMyDeliverUseCase {
    fun getMyDeliver(userId: UUID): DeliverResult
}

interface UpdateDeliverUseCase {
    fun update(command: UpdateDeliverCommand): DeliverResult
}
