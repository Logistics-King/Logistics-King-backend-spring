package logisticsking.com.logisticskingbackendspring.domain.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

enum class EndPointAccessRole {
    PUBLIC,
    ADMIN,
    VENDOR,
    AGENCY,
    DRIVER,
    ;

    companion object {
        fun from(userRole: UserRole): EndPointAccessRole {
            return valueOf(userRole.name)
        }
    }
}
