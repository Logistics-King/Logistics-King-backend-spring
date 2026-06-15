package logisticsking.com.logisticskingbackendspring.infra.mail

import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryEmailSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LoggingAccountRecoveryEmailSender : AccountRecoveryEmailSender {

    override fun sendLoginIdRecoveryEmail(
        email: String,
        loginId: String,
    ) {
        logger.info("Account recovery loginId email requested. email={}, loginId={}", email, loginId)
    }

    override fun sendPasswordResetEmail(
        email: String,
        token: String,
        expiresAt: LocalDateTime,
    ) {
        logger.info("Password reset email requested. email={}, token={}, expiresAt={}", email, token, expiresAt)
    }

    private companion object {
        private val logger = LoggerFactory.getLogger(LoggingAccountRecoveryEmailSender::class.java)
    }
}
