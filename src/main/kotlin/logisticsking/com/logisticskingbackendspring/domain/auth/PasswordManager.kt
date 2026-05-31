package logisticsking.com.logisticskingbackendspring.domain.auth

interface PasswordManager {
    fun encode(rawPassword: String): String

    fun matches(
        rawPassword: String,
        encodedPassword: String,
    ): Boolean
}
