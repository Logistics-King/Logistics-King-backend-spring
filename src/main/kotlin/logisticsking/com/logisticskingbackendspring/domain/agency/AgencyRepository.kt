package logisticsking.com.logisticskingbackendspring.domain.agency

import java.util.UUID

interface AgencyRepository {
    fun save(agency: Agency): Agency
    fun findById(id: UUID): Agency?
    fun findByUserId(userId: UUID): Agency?
    fun existsByUserId(userId: UUID): Boolean
}
