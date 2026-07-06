package logisticsking.com.logisticskingbackendspring.app.driverwork.usecase

import logisticsking.com.logisticskingbackendspring.app.driverwork.command.ApplyDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.AssignDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.CreateDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.DriverWorkIdCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.SelectDriverWorkApplicationCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.result.DriverWorkApplicationResult
import logisticsking.com.logisticskingbackendspring.app.driverwork.result.DriverWorkResult
import logisticsking.com.logisticskingbackendspring.domain.driverwork.DriverWorkSearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CreateDriverWorkUseCase {
    fun create(command: CreateDriverWorkCommand): DriverWorkResult
}

interface GetAgencyDriverWorksUseCase {
    fun getAgencyDriverWorks(
        userId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWorkResult>
}

interface GetOpenDriverWorksUseCase {
    fun getOpenDriverWorks(
        userId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWorkResult>
}

interface GetAssignedDriverWorksUseCase {
    fun getAssignedDriverWorks(
        userId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWorkResult>
}

interface GetDriverWorkApplicationsUseCase {
    fun getApplications(
        userId: UUID,
        driverWorkId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplicationResult>
}

interface GetMyDriverWorkApplicationsUseCase {
    fun getMyApplications(
        userId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplicationResult>
}

interface ApplyDriverWorkUseCase {
    fun apply(command: ApplyDriverWorkCommand): DriverWorkApplicationResult
}

interface WithdrawDriverWorkApplicationUseCase {
    fun withdraw(command: DriverWorkIdCommand): DriverWorkApplicationResult
}

interface SelectDriverWorkApplicationUseCase {
    fun select(command: SelectDriverWorkApplicationCommand): DriverWorkResult
}

interface AssignDriverWorkUseCase {
    fun assign(command: AssignDriverWorkCommand): DriverWorkResult
}

interface CancelDriverWorkUseCase {
    fun cancel(command: DriverWorkIdCommand): DriverWorkResult
}

interface CompleteDriverWorkUseCase {
    fun complete(command: DriverWorkIdCommand): DriverWorkResult
}
