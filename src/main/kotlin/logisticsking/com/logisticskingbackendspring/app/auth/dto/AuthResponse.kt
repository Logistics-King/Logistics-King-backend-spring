package logisticsking.com.logisticskingbackendspring.app.auth.dto

sealed interface AuthResponse {
    data class Login(
        val userId: String,
        val role: String,
    ) : AuthResponse

    data class Refresh(
        val userId: String,
        val role: String,
    ) : AuthResponse

    data class Logout(
        val loggedOut: Boolean,
    ) : AuthResponse
}
