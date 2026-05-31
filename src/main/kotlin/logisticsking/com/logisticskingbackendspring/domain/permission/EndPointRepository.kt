package logisticsking.com.logisticskingbackendspring.domain.permission

import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

interface EndPointRepository {
    fun findByRole(role: UserRole): List<EndPoint>
    fun save(endPoint: EndPoint): EndPoint
}
