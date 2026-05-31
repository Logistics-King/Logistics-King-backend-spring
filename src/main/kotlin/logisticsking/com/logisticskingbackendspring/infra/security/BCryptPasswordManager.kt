package logisticsking.com.logisticskingbackendspring.infra.security

import logisticsking.com.logisticskingbackendspring.domain.auth.PasswordManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordManager(
    private val passwordEncoder: PasswordEncoder,
) : PasswordManager {

    override fun encode(rawPassword: String): String {
        return checkNotNull(passwordEncoder.encode(rawPassword))
    }

    override fun matches(
        rawPassword: String,
        encodedPassword: String,
    ): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}
