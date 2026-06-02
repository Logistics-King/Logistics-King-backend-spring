package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import org.springframework.data.jpa.repository.JpaRepository

interface EndPointJpaRepository : JpaRepository<EndPointJpaEntity, Long> {
    fun findByUrl(url: String): EndPointJpaEntity?
    fun existsByUrl(url: String): Boolean
}
