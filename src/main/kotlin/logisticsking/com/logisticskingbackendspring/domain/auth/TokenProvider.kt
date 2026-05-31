package logisticsking.com.logisticskingbackendspring.domain.auth

import logisticsking.com.logisticskingbackendspring.domain.user.User

interface TokenProvider {
    fun generateAccessToken(user: User): String
    fun generateRefreshToken(user: User): String
    fun parseAccessToken(token: String): TokenClaims
    fun parseRefreshToken(token: String): TokenClaims
}
