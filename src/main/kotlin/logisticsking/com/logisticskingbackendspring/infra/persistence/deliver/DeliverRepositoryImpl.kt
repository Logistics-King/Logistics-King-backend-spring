package logisticsking.com.logisticskingbackendspring.infra.persistence.deliver

import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DeliverRepositoryImpl(
    private val jpaRepository: DeliverJpaRepository,
) : DeliverRepository {

    override fun save(deliver: Deliver): Deliver {
        return jpaRepository.save(DeliverJpaEntity.from(deliver)).toDomain()
    }

    override fun findById(id: UUID): Deliver? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByUserId(userId: UUID): Deliver? {
        return jpaRepository.findByUserId(userId)?.toDomain()
    }

    override fun existsByUserId(userId: UUID): Boolean {
        return jpaRepository.existsByUserId(userId)
    }
}
