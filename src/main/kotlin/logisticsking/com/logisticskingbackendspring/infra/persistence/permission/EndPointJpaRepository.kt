package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import org.springframework.data.jpa.repository.JpaRepository

interface EndPointJpaRepository : JpaRepository<EndPointJpaEntity, Long> {
    fun findByUrlAndMethod(url: String, method: String): EndPointJpaEntity?
}
