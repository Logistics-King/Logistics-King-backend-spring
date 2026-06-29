package logisticsking.com.logisticskingbackendspring.domain.recommendation

enum class RecommendationReasonType(
    val label: String,
) {
    // 과거 계약 이력 기반 추천 사유.
    PREVIOUS_CONTRACT("이전 계약 이력이 있습니다."),

    // 담당 가능 지역과 상대방 주요 지역이 맞는 경우.
    SERVICE_REGION_MATCH("담당 가능 지역과 주요 지역이 매칭됩니다."),

    // 주 담당 지역 또는 주요 지역이 같은 권역인 경우.
    MAIN_REGION_MATCH("주요 지역이 같은 권역입니다."),
}
