package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import java.time.Instant
import java.util.UUID

data class TokenClaims(
    val userId: UUID,

    val role: UserRole,

    val expiresAt: Instant,
)
