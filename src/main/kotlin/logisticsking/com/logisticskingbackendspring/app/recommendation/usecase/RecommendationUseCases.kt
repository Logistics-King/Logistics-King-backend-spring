package logisticsking.com.logisticskingbackendspring.app.recommendation.usecase

import logisticsking.com.logisticskingbackendspring.app.recommendation.result.AgencyRecommendationResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.result.VendorRecommendationResult
import java.util.UUID

interface GetRecommendedAgenciesUseCase {
    fun getRecommendedAgencies(
        userId: UUID,
        limit: Int,
    ): List<AgencyRecommendationResult>
}

interface GetRecommendedVendorsUseCase {
    fun getRecommendedVendors(
        userId: UUID,
        limit: Int,
    ): List<VendorRecommendationResult>
}
