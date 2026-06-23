package logisticsking.com.logisticskingbackendspring.domain.common

enum class BoxSize(
    val label: String,

    val maxTotalLengthCm: Int?,
) {
    SIZE_60("60사이즈", 60),
    SIZE_80("80사이즈", 80),
    SIZE_100("100사이즈", 100),
    SIZE_120("120사이즈", 120),
    SIZE_140("140사이즈", 140),
    SIZE_160("160사이즈", 160),
    ETC("기타", null),
}
