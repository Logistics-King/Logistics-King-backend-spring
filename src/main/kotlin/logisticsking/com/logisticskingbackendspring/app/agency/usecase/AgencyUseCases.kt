package logisticsking.com.logisticskingbackendspring.app.agency.usecase

import logisticsking.com.logisticskingbackendspring.app.agency.command.CreateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.command.UpdateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencySearchCondition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface CreateAgencyUseCase {
    fun create(command: CreateAgencyCommand): AgencyResult
}

interface GetMyAgencyUseCase {
    fun getMyAgency(userId: UUID): AgencyResult
}

interface GetAgenciesUseCase {
    fun getAgencies(
        userId: UUID,
        condition: AgencySearchCondition,
        pageable: Pageable,
    ): Page<AgencyResult>
}

interface GetAgencyUseCase {
    fun getAgency(
        userId: UUID,
        agencyId: UUID,
    ): AgencyResult
}

interface UpdateAgencyUseCase {
    fun update(command: UpdateAgencyCommand): AgencyResult
}
