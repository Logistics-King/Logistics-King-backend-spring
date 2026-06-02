package logisticsking.com.logisticskingbackendspring.domain.agency

enum class Carrier {
    // CJ대한통운
    CJ,

    // 한진택배
    HANJIN,

    // 롯데택배
    LOTTE,

    // 로젠택배
    LOGEN,

    // 우체국택배
    POST_OFFICE,

    // CU 편의점택배 또는 CU 연계 배송
    CU,

    // GS 편의점택배 또는 GS 연계 배송
    GS,

    // 기타 택배사 또는 지역 운송사
    OTHER,
}
