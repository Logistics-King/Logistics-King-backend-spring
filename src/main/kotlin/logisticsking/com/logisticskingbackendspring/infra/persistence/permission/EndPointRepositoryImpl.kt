package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPointRepository
import org.springframework.stereotype.Repository

@Repository
class EndPointRepositoryImpl(
    private val endPointJpaRepository: EndPointJpaRepository,
) : EndPointRepository {

    override fun findAll(): List<EndPoint> {
        return endPointJpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByUrlAndMethod(url: String, method: String): EndPoint? {
        return endPointJpaRepository.findByUrlAndMethod(url, method)?.toDomain()
    }

    override fun save(endPoint: EndPoint): EndPoint {
        return endPointJpaRepository.save(EndPointJpaEntity.from(endPoint)).toDomain()
    }
}
