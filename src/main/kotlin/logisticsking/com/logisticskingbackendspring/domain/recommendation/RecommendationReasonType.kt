package logisticsking.com.logisticskingbackendspring.domain.recommendation

enum class RecommendationReasonType(
    val label: String,
) {
    PREVIOUS_CONTRACT("이전 계약 이력이 있는 대리점입니다."),
    SERVICE_REGION_MATCH("화주 주요 지역을 담당 가능한 대리점입니다."),
    MAIN_REGION_MATCH("화주 주요 지역과 같은 권역의 대리점입니다."),
}
