package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.domain.user.User
import java.util.UUID

interface TokenProvider {
    fun generateAccessToken(user: User): String
    fun generateGuestAccessToken(guestId: UUID): String
    fun generateRefreshToken(user: User): String
    fun parseAccessToken(token: String): TokenClaims
    fun parseRefreshToken(token: String): TokenClaims
}
