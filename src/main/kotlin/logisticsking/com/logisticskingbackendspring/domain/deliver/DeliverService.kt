package logisticsking.com.logisticskingbackendspring.domain.deliver

import logisticsking.com.logisticskingbackendspring.app.deliver.command.CreateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.command.UpdateDeliverCommand
import logisticsking.com.logisticskingbackendspring.app.deliver.result.DeliverResult
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.CreateDeliverUseCase
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.GetAgencyDeliversUseCase
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.GetFreelanceDeliversUseCase
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.GetMyDeliverUseCase
import logisticsking.com.logisticskingbackendspring.app.deliver.usecase.UpdateDeliverUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DeliverService(
    private val userRepository: UserRepository,
    private val agencyRepository: AgencyRepository,
    private val deliverRepository: DeliverRepository,
    private val idGenerator: IdGenerator,
) : CreateDeliverUseCase,
    GetMyDeliverUseCase,
    GetAgencyDeliversUseCase,
    GetFreelanceDeliversUseCase,
    UpdateDeliverUseCase {

    @Transactional
    override fun create(command: CreateDeliverCommand): DeliverResult {
        val user = findDriverUser(command.userId)
        if (deliverRepository.existsByUserId(user.id)) {
            throw GlobalException(DeliverErrorCode.DELIVER_ALREADY_EXISTS)
        }
        ensureAgencyExistsIfNeeded(
            employmentType = command.employmentType,
            agencyId = command.agencyId,
        )

        val deliver = Deliver.create(
            id = idGenerator.generate(),
            userId = user.id,
            employmentType = command.employmentType,
            agencyId = command.agencyId,
            driverName = command.driverName,
            phoneNumber = command.phoneNumber,
            vehicleNumber = command.vehicleNumber,
            serviceRegions = command.serviceRegions,
            active = command.active,
            memo = command.memo,
        )

        return DeliverResult.from(
            deliver = deliverRepository.save(deliver),
            agency = command.agencyId?.let(::findAgency),
        )
    }

    @Transactional(readOnly = true)
    override fun getMyDeliver(userId: UUID): DeliverResult {
        findDriverUser(userId)

        val deliver = findDeliverByUserId(userId)

        return DeliverResult.from(
            deliver = deliver,
            agency = deliver.agencyId?.let(::findAgency),
        )
    }

    @Transactional(readOnly = true)
    override fun getAgencyDelivers(
        userId: UUID,
        condition: DeliverSearchCondition,
        pageable: Pageable,
    ): Page<DeliverResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)

        return deliverRepository.findAllByAgencyId(
            agencyId = agency.id,
            condition = condition,
            pageable = pageable,
        ).map { deliver ->
            DeliverResult.from(
                deliver = deliver,
                agency = deliver.agencyId?.let { agency },
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getFreelanceDelivers(
        userId: UUID,
        condition: DeliverSearchCondition,
        pageable: Pageable,
    ): Page<DeliverResult> {
        findAgencyUser(userId)

        return deliverRepository.findAllFreelancers(
            condition = condition,
            pageable = pageable,
        ).map { deliver ->
            DeliverResult.from(deliver = deliver)
        }
    }

    @Transactional
    override fun update(command: UpdateDeliverCommand): DeliverResult {
        findDriverUser(command.userId)
        ensureAgencyExistsIfNeeded(
            employmentType = command.employmentType,
            agencyId = command.agencyId,
        )
        val deliver = findDeliverByUserId(command.userId)
        val updated = deliver.update(
            employmentType = command.employmentType,
            agencyId = command.agencyId,
            driverName = command.driverName,
            phoneNumber = command.phoneNumber,
            vehicleNumber = command.vehicleNumber,
            serviceRegions = command.serviceRegions,
            active = command.active,
            memo = command.memo,
        )

        return DeliverResult.from(
            deliver = deliverRepository.save(updated),
            agency = command.agencyId?.let(::findAgency),
        )
    }

    private fun findDriverUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(DeliverErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.DRIVER) {
            throw GlobalException(DeliverErrorCode.USER_IS_NOT_DRIVER)
        }

        return user
    }

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(DeliverErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(DeliverErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(DeliverErrorCode.AGENCY_NOT_FOUND)
    }

    private fun ensureAgencyExistsIfNeeded(
        employmentType: DeliverEmploymentType,
        agencyId: UUID?,
    ) {
        if (employmentType == DeliverEmploymentType.AGENCY_AFFILIATED && agencyId == null) {
            throw GlobalException(DeliverErrorCode.AGENCY_REQUIRED_FOR_AFFILIATED_DELIVER)
        }
        if (agencyId != null) {
            agencyRepository.findById(agencyId)
                ?: throw GlobalException(DeliverErrorCode.AGENCY_NOT_FOUND)
        }
    }

    private fun findAgency(agencyId: UUID): Agency {
        return agencyRepository.findById(agencyId)
            ?: throw GlobalException(DeliverErrorCode.AGENCY_NOT_FOUND)
    }

    private fun findDeliverByUserId(userId: UUID): Deliver {
        return deliverRepository.findByUserId(userId)
            ?: throw GlobalException(DeliverErrorCode.DELIVER_NOT_FOUND)
    }
}
