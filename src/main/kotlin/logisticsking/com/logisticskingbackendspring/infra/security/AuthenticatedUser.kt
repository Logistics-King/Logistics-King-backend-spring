package logisticsking.com.logisticskingbackendspring.infra.security

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.util.UUID

data class AuthenticatedUser(
    val userId: UUID,
    val role: UserRole,
)
