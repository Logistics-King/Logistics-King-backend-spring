package logisticsking.com.logisticskingbackendspring.infra.persistence.agency

import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class AgencyRepositoryImpl(
    private val jpaRepository: AgencyJpaRepository,
) : AgencyRepository {

    override fun save(agency: Agency): Agency {
        return jpaRepository.save(AgencyJpaEntity.from(agency)).toDomain()
    }

    override fun findById(id: UUID): Agency? {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)?.toDomain()
    }

    override fun findByUserId(userId: UUID): Agency? {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId)?.toDomain()
    }

    override fun existsByUserId(userId: UUID): Boolean {
        return jpaRepository.existsByUserId(userId)
    }
}
