package logisticsking.com.logisticskingbackendspring.domain.error

open class GlobalException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
) : RuntimeException(message)
