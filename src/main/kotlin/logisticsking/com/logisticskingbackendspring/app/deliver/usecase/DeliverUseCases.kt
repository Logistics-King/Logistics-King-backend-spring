package logisticsking.com.logisticskingbackendspring.app.deliver.usecase

import logisticsking.com.logisticskingbackendspring.app.deliver.command.CreateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.command.UpdateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.result.DeliverResult
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CreateDeliverUseCase {
    fun create(command: CreateDeliverCommand): DeliverResult
}

interface GetMyDeliverUseCase {
    fun getMyDeliver(userId: UUID): DeliverResult
}

interface GetAgencyDeliversUseCase {
    fun getAgencyDelivers(
        userId: UUID,
        condition: DeliverSearchCondition,
        pageable: Pageable,
    ): Page<DeliverResult>
}

interface GetFreelanceDeliversUseCase {
    fun getFreelanceDelivers(
        userId: UUID,
        condition: DeliverSearchCondition,
        pageable: Pageable,
    ): Page<DeliverResult>
}

interface UpdateDeliverUseCase {
    fun update(command: UpdateDeliverCommand): DeliverResult
}
