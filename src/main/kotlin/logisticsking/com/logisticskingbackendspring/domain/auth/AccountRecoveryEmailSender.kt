package logisticsking.com.logisticskingbackendspring.domain.auth

import java.time.LocalDateTime

interface AccountRecoveryEmailSender {
    fun sendLoginIdRecoveryEmail(
        email: String,
        loginId: String,
    )

    fun sendPasswordResetEmail(
        email: String,
        token: String,
        expiresAt: LocalDateTime,
    )
}
