package logisticsking.com.logisticskingbackendspring.domain.error

fun requireDomain(
    condition: Boolean,
    errorCode: ErrorCode,
) {
    if (!condition) {
        throw GlobalException(errorCode)
    }
}
