package logisticsking.com.logisticskingbackendspring.domain.permission

interface EndPointRepository {
    fun findAll(): List<EndPoint>
    fun findByUrlAndMethod(url: String, method: String): EndPoint?
    fun save(endPoint: EndPoint): EndPoint
}
