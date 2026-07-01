package logisticsking.com.logisticskingbackendspring.domain.deliver

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface DeliverRepository {
    fun save(deliver: Deliver): Deliver
    fun findById(id: UUID): Deliver?
    fun findAllByIds(ids: Collection<UUID>): List<Deliver>
    fun findByUserId(userId: UUID): Deliver?
    fun findAllByAgencyId(
        agencyId: UUID,
        condition: DeliverSearchCondition,
        pageable: Pageable,
    ): Page<Deliver>
    fun existsByUserId(userId: UUID): Boolean
}
