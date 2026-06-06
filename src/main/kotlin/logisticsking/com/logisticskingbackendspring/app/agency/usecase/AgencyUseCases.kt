package logisticsking.com.logisticskingbackendspring.app.agency.usecase

import logisticsking.com.logisticskingbackendspring.app.agency.command.CreateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.command.UpdateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import java.util.UUID

interface CreateAgencyUseCase {
    fun create(command: CreateAgencyCommand): AgencyResult
}

interface GetMyAgencyUseCase {
    fun getMyAgency(userId: UUID): AgencyResult
}

interface UpdateAgencyUseCase {
    fun update(command: UpdateAgencyCommand): AgencyResult
}
