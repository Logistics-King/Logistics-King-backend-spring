package logisticsking.com.logisticskingbackendspring.domain.deliver

import java.util.UUID

interface DeliverRepository {
    fun save(deliver: Deliver): Deliver
    fun findById(id: UUID): Deliver?
    fun findByUserId(userId: UUID): Deliver?
    fun existsByUserId(userId: UUID): Boolean
}
