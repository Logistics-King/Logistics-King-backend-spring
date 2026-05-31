package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.io.Serializable

data class EndPointJpaEntityId(
    val url: String = "",
    val role: UserRole = UserRole.VENDOR,
) : Serializable
