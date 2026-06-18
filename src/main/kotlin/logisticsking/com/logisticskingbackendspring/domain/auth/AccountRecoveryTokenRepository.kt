package logisticsking.com.logisticskingbackendspring.domain.auth

import java.time.LocalDateTime
import java.util.UUID

interface AccountRecoveryTokenRepository {
    fun save(token: AccountRecoveryToken): AccountRecoveryToken

    fun findByTokenHashAndPurpose(
        tokenHash: String,
        purpose: AccountRecoveryTokenPurpose,
    ): AccountRecoveryToken?

    fun markUnusedByUserIdAndPurposeAsUsed(
        userId: UUID,
        purpose: AccountRecoveryTokenPurpose,
        usedAt: LocalDateTime,
    )
}
