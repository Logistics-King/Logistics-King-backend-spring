package logisticsking.com.logisticskingbackendspring.domain.auth

interface PasswordManager {
    fun matches(
        rawPassword: String,
        encodedPassword: String,
    ): Boolean
}
