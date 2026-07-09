package logisticsking.com.logisticskingbackendspring.infra.persistence.accesslog

import logisticsking.com.logisticskingbackendspring.domain.accesslog.AccessLog
import logisticsking.com.logisticskingbackendspring.domain.accesslog.AccessLogRepository
import org.springframework.stereotype.Repository

@Repository
class AccessLogRepositoryImpl(
    private val accessLogJpaRepository: AccessLogJpaRepository,
) : AccessLogRepository {

    override fun save(accessLog: AccessLog): AccessLog {
        return accessLogJpaRepository.save(AccessLogJpaEntity.from(accessLog)).toDomain()
    }
}
