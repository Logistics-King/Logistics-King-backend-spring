package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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

    @Column(name = "method", nullable = false, length = 10)
    val method: String,

    @Convert(converter = UserRoleSetConverter::class)
    @Column(name = "roles", nullable = false, columnDefinition = "JSON")
    val roles: Set<UserRole>,

    @Column(name = "description", length = 255)
    val description: String?,
) : BaseJpaEntity() {

    fun toDomain(): EndPoint {
        return EndPoint.restore(
            id = id,
            url = url,
            method = method,
            roles = roles,
            description = description,
        )
    }

    companion object {
        fun from(endPoint: EndPoint): EndPointJpaEntity {
            return EndPointJpaEntity(
                id = endPoint.id,
                url = endPoint.url,
                method = endPoint.method,
                roles = endPoint.roles,
                description = endPoint.description,
            )
        }
    }
}
