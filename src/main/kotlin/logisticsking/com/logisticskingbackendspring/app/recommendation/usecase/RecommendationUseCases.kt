package logisticsking.com.logisticskingbackendspring.app.recommendation.usecase

import logisticsking.com.logisticskingbackendspring.app.recommendation.result.AgencyRecommendationResult
import java.util.UUID

interface GetRecommendedAgenciesUseCase {
    fun getRecommendedAgencies(
        userId: UUID,
        limit: Int,
    ): List<AgencyRecommendationResult>
}
