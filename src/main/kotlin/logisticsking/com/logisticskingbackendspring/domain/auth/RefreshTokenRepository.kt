package logisticsking.com.logisticskingbackendspring.domain.auth

import java.time.Duration
import java.util.UUID

interface RefreshTokenRepository {
    fun save(
        userId: UUID,
        token: String,
        ttl: Duration,
    )

    fun findByUserId(userId: UUID): String?
    fun deleteByUserId(userId: UUID)
}
