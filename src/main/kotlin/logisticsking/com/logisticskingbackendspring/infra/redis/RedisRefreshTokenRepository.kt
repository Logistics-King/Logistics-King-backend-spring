package logisticsking.com.logisticskingbackendspring.infra.redis

import logisticsking.com.logisticskingbackendspring.domain.auth.RefreshTokenRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.UUID

@Repository
class RedisRefreshTokenRepository(
    private val stringRedisTemplate: StringRedisTemplate,
) : RefreshTokenRepository {

    override fun save(
        userId: UUID,
        token: String,
        ttl: Duration,
    ) {
        stringRedisTemplate.opsForValue().set(key(userId), token, ttl)
    }

    override fun findByUserId(userId: UUID): String? {
        return stringRedisTemplate.opsForValue().get(key(userId))
    }

    override fun deleteByUserId(userId: UUID) {
        stringRedisTemplate.delete(key(userId))
    }

    private fun key(userId: UUID): String {
        return "refresh:$userId"
    }
}
