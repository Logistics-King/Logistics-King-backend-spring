package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import java.time.LocalDateTime
import java.util.UUID

class AccountRecoveryToken private constructor(
    val id: UUID,

    val userId: UUID,

    val purpose: AccountRecoveryTokenPurpose,

    val tokenHash: String,

    val expiresAt: LocalDateTime,

    val usedAt: LocalDateTime?,
) {
    fun use(now: LocalDateTime): AccountRecoveryToken {
        if (usedAt != null) {
            throw GlobalException(AuthErrorCode.RESET_TOKEN_INVALID)
        }
        if (!expiresAt.isAfter(now)) {
            throw GlobalException(AuthErrorCode.RESET_TOKEN_EXPIRED)
        }

        return AccountRecoveryToken(
            id = id,
            userId = userId,
            purpose = purpose,
            tokenHash = tokenHash,
            expiresAt = expiresAt,
            usedAt = now,
        )
    }

    companion object {
        fun create(
            id: UUID,
            userId: UUID,
            purpose: AccountRecoveryTokenPurpose,
            tokenHash: String,
            expiresAt: LocalDateTime,
        ): AccountRecoveryToken {
            requireDomain(tokenHash.isNotBlank(), AuthErrorCode.RESET_TOKEN_INVALID)
            requireDomain(expiresAt.isAfter(LocalDateTime.now()), AuthErrorCode.RESET_TOKEN_EXPIRED)

            return AccountRecoveryToken(
                id = id,
                userId = userId,
                purpose = purpose,
                tokenHash = tokenHash,
                expiresAt = expiresAt,
                usedAt = null,
            )
        }

        fun restore(
            id: UUID,
            userId: UUID,
            purpose: AccountRecoveryTokenPurpose,
            tokenHash: String,
            expiresAt: LocalDateTime,
            usedAt: LocalDateTime?,
        ): AccountRecoveryToken {
            return AccountRecoveryToken(
                id = id,
                userId = userId,
                purpose = purpose,
                tokenHash = tokenHash,
                expiresAt = expiresAt,
                usedAt = usedAt,
            )
        }
    }
}
