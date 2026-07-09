package logisticsking.com.logisticskingbackendspring.domain.accesslog

interface AccessLogRepository {
    fun save(accessLog: AccessLog): AccessLog
}
