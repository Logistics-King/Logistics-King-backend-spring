package logisticsking.com.logisticskingbackendspring.domain.agency

import logisticsking.com.logisticskingbackendspring.app.agency.command.CreateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.command.UpdateAgencyCommand
import logisticsking.com.logisticskingbackendspring.app.agency.result.AgencyResult
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.CreateAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.GetMyAgencyUseCase
import logisticsking.com.logisticskingbackendspring.app.agency.usecase.UpdateAgencyUseCase
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AgencyService(
    private val userRepository: UserRepository,
    private val agencyRepository: AgencyRepository,
    private val idGenerator: IdGenerator,
) : CreateAgencyUseCase,
    GetMyAgencyUseCase,
    UpdateAgencyUseCase {

    @Transactional
    override fun create(command: CreateAgencyCommand): AgencyResult {
        val user = findAgencyUser(command.userId)
        if (agencyRepository.existsByUserId(user.id)) {
            throw GlobalException(AgencyErrorCode.AGENCY_ALREADY_EXISTS)
        }

        val agency = Agency.create(
            id = idGenerator.generate(),
            userId = user.id,
            carrier = command.carrier,
            agencyName = command.agencyName,
            businessRegistrationNumber = command.businessRegistrationNumber,
            representativeName = command.representativeName,
            phoneNumber = command.phoneNumber,
            postalCode = command.postalCode,
            address = command.address,
            addressDetail = command.addressDetail,
            mainRegion = command.mainRegion,
            serviceRegions = command.serviceRegions,
            weekdayPickupStartTime = command.weekdayPickupStartTime,
            weekdayPickupEndTime = command.weekdayPickupEndTime,
            saturdayPickupAvailable = command.saturdayPickupAvailable,
            saturdayDeliveryAvailable = command.saturdayDeliveryAvailable,
            returnAvailable = command.returnAvailable,
            coldChainType = command.coldChainType,
            maxMonthlyVolume = command.maxMonthlyVolume,
        )

        return AgencyResult.from(agencyRepository.save(agency))
    }

    @Transactional(readOnly = true)
    override fun getMyAgency(userId: UUID): AgencyResult {
        findAgencyUser(userId)

        return AgencyResult.from(findAgencyByUserId(userId))
    }

    @Transactional
    override fun update(command: UpdateAgencyCommand): AgencyResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val updated = agency.update(
            carrier = command.carrier,
            agencyName = command.agencyName,
            businessRegistrationNumber = command.businessRegistrationNumber,
            representativeName = command.representativeName,
            phoneNumber = command.phoneNumber,
            postalCode = command.postalCode,
            address = command.address,
            addressDetail = command.addressDetail,
            mainRegion = command.mainRegion,
            serviceRegions = command.serviceRegions,
            weekdayPickupStartTime = command.weekdayPickupStartTime,
            weekdayPickupEndTime = command.weekdayPickupEndTime,
            saturdayPickupAvailable = command.saturdayPickupAvailable,
            saturdayDeliveryAvailable = command.saturdayDeliveryAvailable,
            returnAvailable = command.returnAvailable,
            coldChainType = command.coldChainType,
            maxMonthlyVolume = command.maxMonthlyVolume,
        )

        return AgencyResult.from(agencyRepository.save(updated))
    }

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(AgencyErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(AgencyErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(AgencyErrorCode.AGENCY_NOT_FOUND)
    }
}
