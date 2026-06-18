package logisticsking.com.logisticskingbackendspring.infra.security

import logisticsking.com.logisticskingbackendspring.domain.auth.AccountRecoveryTokenGenerator
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.HexFormat

@Component
class SecureAccountRecoveryTokenGenerator(
    private val secureRandom: SecureRandom = SecureRandom(),
) : AccountRecoveryTokenGenerator {

    override fun generate(): String {
        val bytes = ByteArray(TOKEN_BYTES)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    override fun hash(rawToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(rawToken.toByteArray(Charsets.UTF_8))
        return HexFormat.of().formatHex(digest)
    }

    private companion object {
        private const val TOKEN_BYTES = 32
    }
}
