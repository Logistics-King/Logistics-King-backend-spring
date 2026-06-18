package logisticsking.com.logisticskingbackendspring.domain.auth

interface AccountRecoveryTokenGenerator {
    fun generate(): String

    fun hash(rawToken: String): String
}
