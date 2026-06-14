package logisticsking.com.logisticskingbackendspring.infra.persistence.auth

import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryTokenPurpose
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.UUID

interface AccountRecoveryTokenJpaRepository : JpaRepository<AccountRecoveryTokenJpaEntity, UUID> {
    fun findByTokenHashAndPurpose(
        tokenHash: String,
        purpose: AccountRecoveryTokenPurpose,
    ): AccountRecoveryTokenJpaEntity?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        update AccountRecoveryTokenJpaEntity t
        set t.usedAt = :usedAt
        where t.userId = :userId
          and t.purpose = :purpose
          and t.usedAt is null
        """
    )
    fun markUnusedByUserIdAndPurposeAsUsed(
        @Param("userId") userId: UUID,
        @Param("purpose") purpose: AccountRecoveryTokenPurpose,
        @Param("usedAt") usedAt: LocalDateTime,
    ): Int
}
