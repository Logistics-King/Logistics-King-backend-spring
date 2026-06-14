package logisticsking.com.logisticskingbackendspring.infra.persistence.auth

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryToken
import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryTokenPurpose
import logisticsking.com.logisticskingbackendspring.infra.persistence.common.BaseJpaEntity
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "account_recovery_tokens")
class AccountRecoveryTokenJpaEntity(
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false, updatable = false)
    val id: UUID,

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    val userId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    val purpose: AccountRecoveryTokenPurpose,

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    val tokenHash: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Column(name = "used_at")
    val usedAt: LocalDateTime?,
) : BaseJpaEntity() {

    fun toDomain(): AccountRecoveryToken {
        return AccountRecoveryToken.restore(
            id = id,
            userId = userId,
            purpose = purpose,
            tokenHash = tokenHash,
            expiresAt = expiresAt,
            usedAt = usedAt,
        )
    }

    companion object {
        fun from(token: AccountRecoveryToken): AccountRecoveryTokenJpaEntity {
            return AccountRecoveryTokenJpaEntity(
                id = token.id,
                userId = token.userId,
                purpose = token.purpose,
                tokenHash = token.tokenHash,
                expiresAt = token.expiresAt,
                usedAt = token.usedAt,
            )
        }
    }
}
