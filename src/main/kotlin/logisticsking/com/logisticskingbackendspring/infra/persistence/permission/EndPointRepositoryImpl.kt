package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import org.springframework.stereotype.Repository

@Repository
class EndPointRepositoryImpl(
    private val endPointJpaRepository: EndPointJpaRepository,
) : EndPointRepository {

    override fun findByRole(role: UserRole): List<EndPoint> {
        return endPointJpaRepository.findByRole(role).map { it.toDomain() }
    }

    override fun existsByUrlAndRole(
        url: String,
        role: UserRole,
    ): Boolean {
        return endPointJpaRepository.existsByUrlAndRole(
            url = url,
            role = role,
        )
    }

    override fun save(endPoint: EndPoint): EndPoint {
        return endPointJpaRepository.save(EndPointJpaEntity.from(endPoint)).toDomain()
    }
}
