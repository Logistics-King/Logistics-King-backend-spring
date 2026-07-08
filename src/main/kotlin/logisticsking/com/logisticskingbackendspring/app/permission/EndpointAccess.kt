package logisticsking.com.logisticskingbackendspring.app.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EndpointAccess(
    val roles: Array<UserRole> = [UserRole.ADMIN],
    val publicAccess: Boolean = false,
    val description: String = "",
)
