package logisticsking.com.logisticskingbackendspring.infra.persistence.auth

import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryToken
import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryTokenPurpose
import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryTokenRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class AccountRecoveryTokenRepositoryImpl(
    private val jpaRepository: AccountRecoveryTokenJpaRepository,
) : AccountRecoveryTokenRepository {

    override fun save(token: AccountRecoveryToken): AccountRecoveryToken {
        return jpaRepository.save(AccountRecoveryTokenJpaEntity.from(token)).toDomain()
    }

    override fun findByTokenHashAndPurpose(
        tokenHash: String,
        purpose: AccountRecoveryTokenPurpose,
    ): AccountRecoveryToken? {
        return jpaRepository.findByTokenHashAndPurpose(tokenHash, purpose)?.toDomain()
    }

    override fun markUnusedByUserIdAndPurposeAsUsed(
        userId: UUID,
        purpose: AccountRecoveryTokenPurpose,
        usedAt: LocalDateTime,
    ) {
        jpaRepository.markUnusedByUserIdAndPurposeAsUsed(userId, purpose, usedAt)
    }
}
