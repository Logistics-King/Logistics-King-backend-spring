package logisticsking.com.logisticskingbackendspring.infra.persistence.accesslog

import org.springframework.data.jpa.repository.JpaRepository

interface AccessLogJpaRepository : JpaRepository<AccessLogJpaEntity, Long>
