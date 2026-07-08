package logisticsking.com.logisticskingbackendspring.domain.driverwork

import logisticsking.com.logisticskingbackendspring.app.driverwork.command.ApplyDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.AssignDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.CreateDriverWorkCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.DriverWorkIdCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.command.SelectDriverWorkApplicationCommand
import logisticsking.com.logisticskingbackendspring.app.driverwork.result.DriverWorkApplicationResult
import logisticsking.com.logisticskingbackendspring.app.driverwork.result.DriverWorkResult
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.ApplyDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.AssignDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.CancelDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.CompleteDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.CreateDriverWorkUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetAgencyDriverWorksUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetAssignedDriverWorksUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetDriverWorkApplicationsUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetMyDriverWorkApplicationsUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.GetOpenDriverWorksUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.SelectDriverWorkApplicationUseCase
import logisticsking.com.logisticskingbackendspring.app.driverwork.usecase.WithdrawDriverWorkApplicationUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.contract.Contract
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRepository
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverEmploymentType
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverRepository
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationPublisher
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationReferenceType
import logisticsking.com.logisticskingbackendspring.domain.notification.NotificationType
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DriverWorkService(
    private val userRepository: UserRepository,
    private val agencyRepository: AgencyRepository,
    private val contractRepository: ContractRepository,
    private val deliverRepository: DeliverRepository,
    private val driverWorkRepository: DriverWorkRepository,
    private val driverWorkApplicationRepository: DriverWorkApplicationRepository,
    private val notificationPublisher: NotificationPublisher,
    private val idGenerator: IdGenerator,
) : CreateDriverWorkUseCase,
    GetAgencyDriverWorksUseCase,
    GetOpenDriverWorksUseCase,
    GetAssignedDriverWorksUseCase,
    GetDriverWorkApplicationsUseCase,
    GetMyDriverWorkApplicationsUseCase,
    ApplyDriverWorkUseCase,
    WithdrawDriverWorkApplicationUseCase,
    SelectDriverWorkApplicationUseCase,
    AssignDriverWorkUseCase,
    CancelDriverWorkUseCase,
    CompleteDriverWorkUseCase {

    @Transactional
    override fun create(command: CreateDriverWorkCommand): DriverWorkResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val contract = findContract(command.contractId)
        ensureContractBelongsToAgency(contract, agency)
        command.assignedDeliverId?.let { deliverId ->
            ensureAssignedDeliver(agency, findDeliver(deliverId))
        }

        val driverWork = DriverWork.create(
            id = idGenerator.generate(),
            agencyId = agency.id,
            contractId = contract.id,
            title = command.title,
            serviceRegion = command.serviceRegion,
            pickupStartDate = command.pickupStartDate,
            pickupEndDate = command.pickupEndDate,
            expectedVolume = command.expectedVolume,
            unitPrice = command.unitPrice,
            assignedDeliverId = command.assignedDeliverId,
            memo = command.memo,
        )
        val saved = driverWorkRepository.save(driverWork)
        val assignedDeliver = saved.assignedDeliverId?.let(::findDeliver)
        if (assignedDeliver != null) {
            publishToDriver(
                driverWork = saved,
                agency = agency,
                deliver = assignedDeliver,
                type = NotificationType.DRIVER_WORK_ASSIGNED,
            )
        }

        return DriverWorkResult.from(
            driverWork = saved,
            assignedDeliver = assignedDeliver,
        )
    }

    @Transactional(readOnly = true)
    override fun getAgencyDriverWorks(
        userId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWorkResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)

        return driverWorkRepository.findAllByAgencyId(
            agencyId = agency.id,
            condition = condition,
            pageable = pageable,
        ).toDriverWorkResults()
    }

    @Transactional(readOnly = true)
    override fun getOpenDriverWorks(
        userId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWorkResult> {
        findDriverUser(userId)
        val deliver = findDeliverByUserId(userId)
        ensureAgencyAffiliatedDeliver(deliver)

        return driverWorkRepository.findOpenByAgencyId(
            agencyId = deliver.agencyId ?: throw GlobalException(DriverWorkErrorCode.DELIVER_DOES_NOT_BELONG_TO_AGENCY),
            condition = condition,
            pageable = pageable,
        ).toDriverWorkResults()
    }

    @Transactional(readOnly = true)
    override fun getAssignedDriverWorks(
        userId: UUID,
        condition: DriverWorkSearchCondition,
        pageable: Pageable,
    ): Page<DriverWorkResult> {
        findDriverUser(userId)
        val deliver = findDeliverByUserId(userId)

        return driverWorkRepository.findAssignedByDeliverId(
            deliverId = deliver.id,
            condition = condition,
            pageable = pageable,
        ).toDriverWorkResults()
    }

    @Transactional(readOnly = true)
    override fun getApplications(
        userId: UUID,
        driverWorkId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplicationResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)
        val driverWork = findDriverWorkByAgency(driverWorkId, agency.id)

        return driverWorkApplicationRepository.findAllByDriverWorkId(driverWork.id, pageable)
            .toApplicationResults(driverWork = driverWork)
    }

    @Transactional(readOnly = true)
    override fun getMyApplications(
        userId: UUID,
        pageable: Pageable,
    ): Page<DriverWorkApplicationResult> {
        findDriverUser(userId)
        val deliver = findDeliverByUserId(userId)

        return driverWorkApplicationRepository.findAllByDeliverId(deliver.id, pageable)
            .toApplicationResults()
    }

    @Transactional
    override fun apply(command: ApplyDriverWorkCommand): DriverWorkApplicationResult {
        findDriverUser(command.userId)
        val deliver = findDeliverByUserId(command.userId)
        ensureAgencyAffiliatedDeliver(deliver)
        val driverWork = findDriverWork(command.driverWorkId)
        ensureDeliverBelongsToAgency(deliver, driverWork.agencyId)
        if (driverWork.status != DriverWorkStatus.OPEN) {
            throw GlobalException(DriverWorkErrorCode.ONLY_OPEN_WORK_CAN_BE_APPLIED)
        }
        if (driverWorkApplicationRepository.existsAppliedByDriverWorkIdAndDeliverId(driverWork.id, deliver.id)) {
            throw GlobalException(DriverWorkErrorCode.ALREADY_APPLIED)
        }

        val application = DriverWorkApplication.create(
            id = idGenerator.generate(),
            driverWorkId = driverWork.id,
            deliverId = deliver.id,
            memo = command.memo,
        )
        val saved = driverWorkApplicationRepository.save(application)
        publishToAgency(
            driverWork = driverWork,
            deliver = deliver,
            type = NotificationType.DRIVER_WORK_APPLIED,
        )

        return DriverWorkApplicationResult.from(
            application = saved,
            deliver = deliver,
            driverWork = driverWork,
        )
    }

    @Transactional
    override fun withdraw(command: DriverWorkIdCommand): DriverWorkApplicationResult {
        findDriverUser(command.userId)
        val deliver = findDeliverByUserId(command.userId)
        val application = driverWorkApplicationRepository.findByDriverWorkIdAndDeliverId(
            driverWorkId = command.driverWorkId,
            deliverId = deliver.id,
        ) ?: throw GlobalException(DriverWorkErrorCode.APPLICATION_NOT_FOUND)
        val saved = driverWorkApplicationRepository.save(application.withdraw())

        return DriverWorkApplicationResult.from(
            application = saved,
            deliver = deliver,
            driverWork = findDriverWork(saved.driverWorkId),
        )
    }

    @Transactional
    override fun select(command: SelectDriverWorkApplicationCommand): DriverWorkResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val driverWork = findDriverWorkByAgency(command.driverWorkId, agency.id)
        val application = driverWorkApplicationRepository.findByIdAndDriverWorkId(
            id = command.applicationId,
            driverWorkId = driverWork.id,
        ) ?: throw GlobalException(DriverWorkErrorCode.APPLICATION_NOT_FOUND)
        val deliver = findDeliver(application.deliverId)
        ensureAssignedDeliver(agency, deliver)
        val savedWork = driverWorkRepository.save(driverWork.assign(deliver.id))
        val applications = driverWorkApplicationRepository.findAllByDriverWorkId(driverWork.id)
        driverWorkApplicationRepository.saveAll(
            applications.map { current ->
                when {
                    current.id == application.id -> current.select()
                    current.status == DriverWorkApplicationStatus.APPLIED -> current.reject()
                    else -> current
                }
            }
        )
        publishToDriver(
            driverWork = savedWork,
            agency = agency,
            deliver = deliver,
            type = NotificationType.DRIVER_WORK_SELECTED,
        )

        return DriverWorkResult.from(
            driverWork = savedWork,
            assignedDeliver = deliver,
        )
    }

    @Transactional
    override fun assign(command: AssignDriverWorkCommand): DriverWorkResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val driverWork = findDriverWorkByAgency(command.driverWorkId, agency.id)
        val deliver = findDeliver(command.deliverId)
        ensureAssignedDeliver(agency, deliver)
        val saved = driverWorkRepository.save(driverWork.assign(deliver.id))
        rejectAppliedApplications(driverWork.id)
        publishToDriver(
            driverWork = saved,
            agency = agency,
            deliver = deliver,
            type = NotificationType.DRIVER_WORK_ASSIGNED,
        )

        return DriverWorkResult.from(
            driverWork = saved,
            assignedDeliver = deliver,
        )
    }

    @Transactional
    override fun cancel(command: DriverWorkIdCommand): DriverWorkResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val driverWork = findDriverWorkByAgency(command.driverWorkId, agency.id)
        val saved = driverWorkRepository.save(driverWork.cancel())
        rejectAppliedApplications(driverWork.id)

        return DriverWorkResult.from(saved)
    }

    @Transactional
    override fun complete(command: DriverWorkIdCommand): DriverWorkResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val driverWork = findDriverWorkByAgency(command.driverWorkId, agency.id)
        val saved = driverWorkRepository.save(driverWork.complete())

        return DriverWorkResult.from(
            driverWork = saved,
            assignedDeliver = saved.assignedDeliverId?.let(::findDeliver),
        )
    }

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(DriverWorkErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(DriverWorkErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findDriverUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(DriverWorkErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.DRIVER) {
            throw GlobalException(DriverWorkErrorCode.USER_IS_NOT_DRIVER)
        }

        return user
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(DriverWorkErrorCode.AGENCY_NOT_FOUND)
    }

    private fun findAgency(agencyId: UUID): Agency {
        return agencyRepository.findById(agencyId)
            ?: throw GlobalException(DriverWorkErrorCode.AGENCY_NOT_FOUND)
    }

    private fun findContract(contractId: UUID): Contract {
        return contractRepository.findById(contractId)
            ?: throw GlobalException(DriverWorkErrorCode.CONTRACT_NOT_FOUND)
    }

    private fun findDeliver(deliverId: UUID): Deliver {
        return deliverRepository.findById(deliverId)
            ?: throw GlobalException(DriverWorkErrorCode.DELIVER_NOT_FOUND)
    }

    private fun findDeliverByUserId(userId: UUID): Deliver {
        return deliverRepository.findByUserId(userId)
            ?: throw GlobalException(DriverWorkErrorCode.DELIVER_NOT_FOUND)
    }

    private fun findDriverWork(driverWorkId: UUID): DriverWork {
        return driverWorkRepository.findById(driverWorkId)
            ?: throw GlobalException(DriverWorkErrorCode.NOT_FOUND)
    }

    private fun findDriverWorkByAgency(
        driverWorkId: UUID,
        agencyId: UUID,
    ): DriverWork {
        return driverWorkRepository.findByIdAndAgencyId(
            id = driverWorkId,
            agencyId = agencyId,
        ) ?: throw GlobalException(DriverWorkErrorCode.NOT_FOUND)
    }

    private fun ensureContractBelongsToAgency(
        contract: Contract,
        agency: Agency,
    ) {
        if (contract.agencyId != agency.id) {
            throw GlobalException(DriverWorkErrorCode.CONTRACT_DOES_NOT_BELONG_TO_AGENCY)
        }
    }

    private fun ensureAssignedDeliver(
        agency: Agency,
        deliver: Deliver,
    ) {
        ensureAgencyAffiliatedDeliver(deliver)
        ensureDeliverBelongsToAgency(deliver, agency.id)
    }

    private fun ensureAgencyAffiliatedDeliver(deliver: Deliver) {
        if (deliver.employmentType != DeliverEmploymentType.AGENCY_AFFILIATED) {
            throw GlobalException(DriverWorkErrorCode.DELIVER_IS_NOT_AGENCY_AFFILIATED)
        }
    }

    private fun ensureDeliverBelongsToAgency(
        deliver: Deliver,
        agencyId: UUID,
    ) {
        if (deliver.agencyId != agencyId) {
            throw GlobalException(DriverWorkErrorCode.DELIVER_DOES_NOT_BELONG_TO_AGENCY)
        }
    }

    private fun Page<DriverWork>.toDriverWorkResults(): Page<DriverWorkResult> {
        val deliversById = deliverRepository.findAllByIds(
            content.mapNotNull(DriverWork::assignedDeliverId).distinct()
        ).associateBy(Deliver::id)

        return map { driverWork ->
            DriverWorkResult.from(
                driverWork = driverWork,
                assignedDeliver = driverWork.assignedDeliverId?.let(deliversById::get),
            )
        }
    }

    private fun Page<DriverWorkApplication>.toApplicationResults(
        driverWork: DriverWork? = null,
    ): Page<DriverWorkApplicationResult> {
        val deliversById = deliverRepository.findAllByIds(content.map(DriverWorkApplication::deliverId).distinct())
            .associateBy(Deliver::id)
        val worksById = if (driverWork != null) {
            mapOf(driverWork.id to driverWork)
        } else {
            content.map(DriverWorkApplication::driverWorkId)
                .distinct()
                .mapNotNull(driverWorkRepository::findById)
                .associateBy(DriverWork::id)
        }

        return map { application ->
            DriverWorkApplicationResult.from(
                application = application,
                deliver = deliversById[application.deliverId],
                driverWork = worksById[application.driverWorkId],
            )
        }
    }

    private fun rejectAppliedApplications(driverWorkId: UUID) {
        val applications = driverWorkApplicationRepository.findAllByDriverWorkId(driverWorkId)
        driverWorkApplicationRepository.saveAll(
            applications.map { application ->
                if (application.status == DriverWorkApplicationStatus.APPLIED) {
                    application.reject()
                } else {
                    application
                }
            }
        )
    }

    private fun publishToDriver(
        driverWork: DriverWork,
        agency: Agency,
        deliver: Deliver,
        type: NotificationType,
    ) {
        notificationPublisher.publish(
            receiverUserId = deliver.userId,
            senderUserId = agency.userId,
            type = type,
            referenceType = NotificationReferenceType.DRIVER_WORK,
            referenceId = driverWork.id,
            linkUrl = "/driver-works/driver/me/assigned",
        )
    }

    private fun publishToAgency(
        driverWork: DriverWork,
        deliver: Deliver,
        type: NotificationType,
    ) {
        val agency = findAgency(driverWork.agencyId)
        notificationPublisher.publish(
            receiverUserId = agency.userId,
            senderUserId = deliver.userId,
            type = type,
            referenceType = NotificationReferenceType.DRIVER_WORK,
            referenceId = driverWork.id,
            linkUrl = "/driver-works/agency/me",
        )
    }
}
