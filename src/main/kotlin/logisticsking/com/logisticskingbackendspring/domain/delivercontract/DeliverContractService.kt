package logisticsking.com.logisticskingbackendspring.domain.delivercontract

import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.CreateDeliverContractCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.DeliverContractIdCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.command.UpdateDeliverContractCommand
import logisticsking.com.logisticskingbackendspring.app.delivercontract.result.DeliverContractResult
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.AcceptDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.CancelDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.CreateDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.GetMyAgencyDeliverContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.GetMyDriverDeliverContractsUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.RejectDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.app.delivercontract.usecase.UpdateDeliverContractUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
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
class DeliverContractService(
    private val userRepository: UserRepository,
    private val agencyRepository: AgencyRepository,
    private val deliverRepository: DeliverRepository,
    private val deliverContractRepository: DeliverContractRepository,
    private val notificationPublisher: NotificationPublisher,
    private val idGenerator: IdGenerator,
) : CreateDeliverContractUseCase,
    GetMyAgencyDeliverContractsUseCase,
    GetMyDriverDeliverContractsUseCase,
    UpdateDeliverContractUseCase,
    AcceptDeliverContractUseCase,
    RejectDeliverContractUseCase,
    CancelDeliverContractUseCase {

    @Transactional
    override fun create(command: CreateDeliverContractCommand): DeliverContractResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val deliver = findDeliver(command.deliverId)
        ensureDeliverBelongsToAgency(deliver, agency)
        if (deliverContractRepository.existsActiveByAgencyIdAndDeliverId(agency.id, deliver.id)) {
            throw GlobalException(DeliverContractErrorCode.ALREADY_EXISTS)
        }

        val deliverContract = DeliverContract.create(
            id = idGenerator.generate(),
            agencyId = agency.id,
            deliverId = deliver.id,
            serviceRegion = command.serviceRegion,
            expectedMonthlyVolume = command.expectedMonthlyVolume,
            unitPrice = command.unitPrice,
            startDate = command.startDate,
            endDate = command.endDate,
            memo = command.memo,
        )

        val saved = deliverContractRepository.save(deliverContract)
        notificationPublisher.publish(
            receiverUserId = deliver.userId,
            senderUserId = agency.userId,
            type = NotificationType.DELIVER_CONTRACT_REQUESTED,
            referenceType = NotificationReferenceType.DELIVER_CONTRACT,
            referenceId = saved.id,
            linkUrl = "/deliver-contracts/driver/me",
        )

        return DeliverContractResult.from(saved)
    }

    @Transactional(readOnly = true)
    override fun getMyAgencyDeliverContracts(userId: UUID, pageable: Pageable): Page<DeliverContractResult> {
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)

        return deliverContractRepository.findAllByAgencyId(agency.id, pageable)
            .map(DeliverContractResult::from)
    }

    @Transactional(readOnly = true)
    override fun getMyDriverDeliverContracts(userId: UUID, pageable: Pageable): Page<DeliverContractResult> {
        findDriverUser(userId)
        val deliver = findDeliverByUserId(userId)

        return deliverContractRepository.findAllByDeliverId(deliver.id, pageable)
            .map(DeliverContractResult::from)
    }

    @Transactional
    override fun update(command: UpdateDeliverContractCommand): DeliverContractResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val deliverContract = findDeliverContractByAgency(command.deliverContractId, agency.id)
        val updated = deliverContract.update(
            serviceRegion = command.serviceRegion,
            expectedMonthlyVolume = command.expectedMonthlyVolume,
            unitPrice = command.unitPrice,
            startDate = command.startDate,
            endDate = command.endDate,
            memo = command.memo,
        )

        return DeliverContractResult.from(deliverContractRepository.save(updated))
    }

    @Transactional
    override fun accept(command: DeliverContractIdCommand): DeliverContractResult {
        findDriverUser(command.userId)
        val deliver = findDeliverByUserId(command.userId)
        val deliverContract = findDeliverContractByDeliver(command.deliverContractId, deliver.id)

        val saved = deliverContractRepository.save(deliverContract.accept())
        notificationPublisher.publish(
            receiverUserId = findAgency(saved.agencyId).userId,
            senderUserId = deliver.userId,
            type = NotificationType.DELIVER_CONTRACT_ACCEPTED,
            referenceType = NotificationReferenceType.DELIVER_CONTRACT,
            referenceId = saved.id,
            linkUrl = "/deliver-contracts/agency/me",
        )

        return DeliverContractResult.from(saved)
    }

    @Transactional
    override fun reject(command: DeliverContractIdCommand): DeliverContractResult {
        findDriverUser(command.userId)
        val deliver = findDeliverByUserId(command.userId)
        val deliverContract = findDeliverContractByDeliver(command.deliverContractId, deliver.id)

        val saved = deliverContractRepository.save(deliverContract.reject())
        notificationPublisher.publish(
            receiverUserId = findAgency(saved.agencyId).userId,
            senderUserId = deliver.userId,
            type = NotificationType.DELIVER_CONTRACT_REJECTED,
            referenceType = NotificationReferenceType.DELIVER_CONTRACT,
            referenceId = saved.id,
            linkUrl = "/deliver-contracts/agency/me",
        )

        return DeliverContractResult.from(saved)
    }

    @Transactional
    override fun cancel(command: DeliverContractIdCommand): DeliverContractResult {
        findAgencyUser(command.userId)
        val agency = findAgencyByUserId(command.userId)
        val deliverContract = findDeliverContractByAgency(command.deliverContractId, agency.id)

        return DeliverContractResult.from(deliverContractRepository.save(deliverContract.cancel()))
    }

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(DeliverContractErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(DeliverContractErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findDriverUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(DeliverContractErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.DRIVER) {
            throw GlobalException(DeliverContractErrorCode.USER_IS_NOT_DRIVER)
        }

        return user
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(DeliverContractErrorCode.AGENCY_NOT_FOUND)
    }

    private fun findDeliver(deliverId: UUID): Deliver {
        return deliverRepository.findById(deliverId)
            ?: throw GlobalException(DeliverContractErrorCode.DELIVER_NOT_FOUND)
    }

    private fun findAgency(agencyId: UUID): Agency {
        return agencyRepository.findById(agencyId)
            ?: throw GlobalException(DeliverContractErrorCode.AGENCY_NOT_FOUND)
    }

    private fun findDeliverByUserId(userId: UUID): Deliver {
        return deliverRepository.findByUserId(userId)
            ?: throw GlobalException(DeliverContractErrorCode.DELIVER_NOT_FOUND)
    }

    private fun findDeliverContractByAgency(
        deliverContractId: UUID,
        agencyId: UUID,
    ): DeliverContract {
        return deliverContractRepository.findByIdAndAgencyId(
            id = deliverContractId,
            agencyId = agencyId,
        ) ?: throw GlobalException(DeliverContractErrorCode.NOT_FOUND)
    }

    private fun findDeliverContractByDeliver(
        deliverContractId: UUID,
        deliverId: UUID,
    ): DeliverContract {
        return deliverContractRepository.findByIdAndDeliverId(
            id = deliverContractId,
            deliverId = deliverId,
        ) ?: throw GlobalException(DeliverContractErrorCode.NOT_FOUND)
    }

    private fun ensureDeliverBelongsToAgency(
        deliver: Deliver,
        agency: Agency,
    ) {
        if (deliver.agencyId != agency.id) {
            throw GlobalException(DeliverContractErrorCode.DELIVER_DOES_NOT_BELONG_TO_AGENCY)
        }
    }
}
