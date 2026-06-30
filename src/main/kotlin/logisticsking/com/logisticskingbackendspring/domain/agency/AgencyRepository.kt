package logisticsking.com.logisticskingbackendspring.domain.agency

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface AgencyRepository {
    fun save(agency: Agency): Agency
    fun findById(id: UUID): Agency?
    fun findAllByIds(ids: Collection<UUID>): List<Agency>
    fun findAll(
        condition: AgencySearchCondition,
        pageable: Pageable,
    ): Page<Agency>
    fun findAllForRecommendation(): List<Agency>
    fun findByUserId(userId: UUID): Agency?
    fun existsByUserId(userId: UUID): Boolean
}
