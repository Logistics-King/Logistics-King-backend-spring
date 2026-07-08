package logisticsking.com.logisticskingbackendspring.domain.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

class EndPoint private constructor(
    val id: Long,

    val url: String,

    val method: String,

    val roles: Set<EndPointAccessRole>,

    val description: String?,
) {
    fun allows(role: UserRole): Boolean {
        return isPublic() || roles.contains(EndPointAccessRole.from(role))
    }

    fun isPublic(): Boolean {
        return roles.contains(EndPointAccessRole.PUBLIC)
    }

    companion object {
        fun create(
            url: String,
            method: String,
            roles: Set<EndPointAccessRole>,
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
            roles: Set<EndPointAccessRole>,
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
