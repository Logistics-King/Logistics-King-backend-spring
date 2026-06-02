package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import jakarta.persistence.Column
import jakarta.persistence.CollectionTable
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.permission.EndPoint
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity

@Entity
@Table(name = "end_points")
class EndPointJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long,

    @Column(name = "url", nullable = false, length = 255)
    val url: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "end_point_roles",
        joinColumns = [JoinColumn(name = "end_point_id")],
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    val roles: Set<UserRole>,

    @Column(name = "description", length = 255)
    val description: String?,
) : BaseJpaEntity() {

    fun toDomain(): EndPoint {
        return EndPoint.restore(
            id = id,
            url = url,
            roles = roles,
            description = description,
        )
    }

    companion object {
        fun from(endPoint: EndPoint): EndPointJpaEntity {
            return EndPointJpaEntity(
                id = endPoint.id,
                url = endPoint.url,
                roles = endPoint.roles,
                description = endPoint.description,
            )
        }
    }
}
