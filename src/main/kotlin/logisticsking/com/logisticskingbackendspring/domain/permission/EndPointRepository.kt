package logisticsking.com.logisticskingbackendspring.domain.permission

interface EndPointRepository {
    fun findAll(): List<EndPoint>
    fun findByUrl(url: String): EndPoint?
    fun existsByUrl(url: String): Boolean
    fun save(endPoint: EndPoint): EndPoint
}
