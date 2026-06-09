package logisticsking.com.logisticskingbackendspring.infra.persistence.deliver

import logisticsking.com.logisticskingbackendspring.domain.deliver.Deliver
import logisticsking.com.logisticskingbackendspring.domain.deliver.DeliverRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
        return jpaRepository.findByIdAndDeletedAtIsNull(id)?.toDomain()
    }

    override fun findByUserId(userId: UUID): Deliver? {
        return jpaRepository.findByUserIdAndDeletedAtIsNull(userId)?.toDomain()
    }

    override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Deliver> {
        return jpaRepository.findAllByAgencyIdAndDeletedAtIsNullOrderByCreatedAtDesc(agencyId, pageable)
            .map(DeliverJpaEntity::toDomain)
    }

    override fun existsByUserId(userId: UUID): Boolean {
        return jpaRepository.existsByUserId(userId)
    }
}
