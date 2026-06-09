package logisticsking.com.logisticskingbackendspring.infra.persistence.common

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class SoftDeletableJpaEntity : BaseJpaEntity() {

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

    fun softDelete(now: LocalDateTime = LocalDateTime.now()) {
        deletedAt = now
    }

    fun restore() {
        deletedAt = null
    }

    fun isDeleted(): Boolean {
        return deletedAt != null
    }
}
