package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity

@Entity
@IdClass(EndPointJpaEntityId::class)
@Table(name = "end_points")
class EndPointJpaEntity(
    @Id
    @Column(name = "url", nullable = false, length = 255)
    val url: String,

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    val role: UserRole,

    @Column(name = "description", length = 255)
    val description: String?,
) : BaseJpaEntity() {

    fun toDomain(): EndPoint {
        return EndPoint.restore(
            url = url,
            role = role,
            description = description,
        )
    }

    companion object {
        fun from(endPoint: EndPoint): EndPointJpaEntity {
            return EndPointJpaEntity(
                url = endPoint.url,
                role = endPoint.role,
                description = endPoint.description,
            )
        }
    }
}
