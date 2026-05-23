package logisticsking.com.logisticskingbackendspring.domain.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

class EndPoint private constructor(
    val url: String,
    val role: UserRole,
    val description: String?,
) {
    companion object {
        fun create(
            url: String,
            role: UserRole,
            description: String?,
        ): EndPoint {
            return EndPoint(
                url = url,
                role = role,
                description = description,
            )
        }

        fun restore(
            url: String,
            role: UserRole,
            description: String?,
        ): EndPoint {
            return EndPoint(
                url = url,
                role = role,
                description = description,
            )
        }
    }
}
