package logisticsking.com.logisticskingbackendspring.domain.recommendation

enum class RecommendationPurpose {
    // 화주가 계약 요청을 만들거나 대리점을 탐색할 때 사용하는 추천.
    CONTRACT_REQUEST_AGENCY_MATCHING,

    // 대리점이 영업 대상 화주를 찾을 때 사용하는 추천.
    AGENCY_SALES_VENDOR_DISCOVERY,

    // 이후 대리점이 배송기사 배정 후보를 찾을 때 확장할 추천 목적.
    DRIVER_ASSIGNMENT,
}
