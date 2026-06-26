package logisticsking.com.logisticskingbackendspring.domain.recommendation

import logisticsking.com.logisticskingbackendspring.app.recommendation.result.AgencyRecommendationResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.result.RecommendationReasonResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.usecase.GetRecommendedAgenciesUseCase
import logisticsking.com.logisticskingbackendspring.domain.agency.Agency
import logisticsking.com.logisticskingbackendspring.domain.agency.AgencyRepository
import logisticsking.com.logisticskingbackendspring.domain.contract.ContractRepository
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.user.User
import logisticsking.com.logisticskingbackendspring.domain.user.UserRepository
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole
import logisticsking.com.logisticskingbackendspring.domain.vendor.Vendor
import logisticsking.com.logisticskingbackendspring.domain.vendor.VendorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RecommendationService(
    private val userRepository: UserRepository,
    private val vendorRepository: VendorRepository,
    private val agencyRepository: AgencyRepository,
    private val contractRepository: ContractRepository,
) : GetRecommendedAgenciesUseCase {

    @Transactional(readOnly = true)
    override fun getRecommendedAgencies(
        userId: UUID,
        limit: Int,
    ): List<AgencyRecommendationResult> {
        if (limit !in MIN_LIMIT..MAX_LIMIT) {
            throw GlobalException(RecommendationErrorCode.INVALID_LIMIT)
        }

        findVendorUser(userId)
        val vendor = findVendorByUserId(userId)
        val previousAgencyIds = contractRepository.findRecentAgencyIdsByVendorId(
            vendorId = vendor.id,
            limit = PREVIOUS_CONTRACT_LOOKUP_LIMIT,
        )
        val previousAgencyRank = previousAgencyIds.withIndex()
            .associate { (index, agencyId) -> agencyId to index }

        return agencyRepository.findAllForRecommendation()
            .mapNotNull { agency -> agency.toRecommendation(vendor, previousAgencyRank) }
            .sortedWith(
                compareByDescending<AgencyRecommendationResult> { it.score }
                    .thenBy { previousAgencyRank[it.agencyId] ?: Int.MAX_VALUE }
                    .thenBy { it.agencyName }
            )
            .take(limit)
    }

    private fun Agency.toRecommendation(
        vendor: Vendor,
        previousAgencyRank: Map<UUID, Int>,
    ): AgencyRecommendationResult? {
        val reasons = mutableListOf<RecommendationReasonResult>()
        var score = 0

        if (id in previousAgencyRank) {
            score += PREVIOUS_CONTRACT_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.PREVIOUS_CONTRACT)
        }

        if (serviceRegions.any { it.regionMatches(vendor.mainRegion) }) {
            score += SERVICE_REGION_MATCH_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.SERVICE_REGION_MATCH)
        }

        if (mainRegion.regionMatches(vendor.mainRegion)) {
            score += MAIN_REGION_MATCH_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.MAIN_REGION_MATCH)
        }

        if (score == 0) {
            return null
        }

        return AgencyRecommendationResult.from(
            agency = this,
            score = score,
            reasons = reasons,
        )
    }

    private fun String.regionMatches(other: String): Boolean {
        val source = trim()
        val target = other.trim()

        return source.isNotBlank() &&
            target.isNotBlank() &&
            (source.equals(target, ignoreCase = true) ||
                source.contains(target, ignoreCase = true) ||
                target.contains(source, ignoreCase = true))
    }

    private fun findVendorUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(RecommendationErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.VENDOR) {
            throw GlobalException(RecommendationErrorCode.USER_IS_NOT_VENDOR)
        }

        return user
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(RecommendationErrorCode.VENDOR_NOT_FOUND)
    }

    companion object {
        private const val MIN_LIMIT = 1
        private const val MAX_LIMIT = 50
        private const val PREVIOUS_CONTRACT_LOOKUP_LIMIT = 20
        private const val PREVIOUS_CONTRACT_SCORE = 100
        private const val SERVICE_REGION_MATCH_SCORE = 50
        private const val MAIN_REGION_MATCH_SCORE = 30
    }
}
