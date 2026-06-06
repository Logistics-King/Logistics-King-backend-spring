package logisticsking.com.logisticskingbackendspring.domain.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

class EndPoint private constructor(
    val id: Long,
    val url: String,
    val method: String,
    val roles: Set<UserRole>,
    val description: String?,
) {
    fun allows(role: UserRole): Boolean {
        return roles.contains(role)
    }

    companion object {
        fun create(
            url: String,
            method: String,
            roles: Set<UserRole>,
            description: String?,
        ): EndPoint {
            return EndPoint(
                id = 0,
                url = url,
                method = method,
                roles = roles,
                description = description,
            )
        }

        fun restore(
            id: Long,
            url: String,
            method: String,
            roles: Set<UserRole>,
            description: String?,
        ): EndPoint {
            return EndPoint(
                id = id,
                url = url,
                method = method,
                roles = roles,
                description = description,
            )
        }
    }
}
