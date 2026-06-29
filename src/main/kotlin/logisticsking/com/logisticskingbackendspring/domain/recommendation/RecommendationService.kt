package logisticsking.com.logisticskingbackendspring.domain.recommendation

import logisticsking.com.logisticskingbackendspring.app.recommendation.result.AgencyRecommendationResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.result.RecommendationReasonResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.result.VendorRecommendationResult
import logisticsking.com.logisticskingbackendspring.app.recommendation.usecase.GetRecommendedAgenciesUseCase
import logisticsking.com.logisticskingbackendspring.app.recommendation.usecase.GetRecommendedVendorsUseCase
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
) : GetRecommendedAgenciesUseCase,
    GetRecommendedVendorsUseCase {

    @Transactional(readOnly = true)
    override fun getRecommendedAgencies(
        userId: UUID,
        limit: Int,
    ): List<AgencyRecommendationResult> {
        if (limit !in MIN_LIMIT..MAX_LIMIT) {
            throw GlobalException(RecommendationErrorCode.INVALID_LIMIT)
        }

        // 화주 -> 대리점 추천.
        // 현재는 별도 추천 테이블을 두지 않고, 화주의 현재 프로필과 과거 계약 이력,
        // 대리점 담당 지역을 조회 시점에 조합해 rule-based 점수를 계산한다.
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

    @Transactional(readOnly = true)
    override fun getRecommendedVendors(
        userId: UUID,
        limit: Int,
    ): List<VendorRecommendationResult> {
        if (limit !in MIN_LIMIT..MAX_LIMIT) {
            throw GlobalException(RecommendationErrorCode.INVALID_LIMIT)
        }

        // 대리점 -> 화주 추천.
        // 화주 추천도 같은 Recommendation 도메인 안에서 requester/target만 바뀌는 구조다.
        // 이후 배송기사 추천도 같은 패턴으로 target만 DRIVER로 확장할 수 있다.
        findAgencyUser(userId)
        val agency = findAgencyByUserId(userId)
        val previousVendorIds = contractRepository.findRecentVendorIdsByAgencyId(
            agencyId = agency.id,
            limit = PREVIOUS_CONTRACT_LOOKUP_LIMIT,
        )
        val previousVendorRank = previousVendorIds.withIndex()
            .associate { (index, vendorId) -> vendorId to index }

        return vendorRepository.findAllForRecommendation()
            .mapNotNull { vendor -> vendor.toRecommendation(agency, previousVendorRank) }
            .sortedWith(
                compareByDescending<VendorRecommendationResult> { it.score }
                    .thenBy { previousVendorRank[it.vendorId] ?: Int.MAX_VALUE }
                    .thenBy { it.businessName }
            )
            .take(limit)
    }

    private fun Agency.toRecommendation(
        vendor: Vendor,
        previousAgencyRank: Map<UUID, Int>,
    ): AgencyRecommendationResult? {
        val reasons = mutableListOf<RecommendationReasonResult>()
        var score = 0

        // 이전 계약 이력은 재계약 가능성이 가장 높은 신호라 가장 큰 점수를 준다.
        if (id in previousAgencyRank) {
            score += PREVIOUS_CONTRACT_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.PREVIOUS_CONTRACT)
        }

        // 대리점의 담당 가능 지역에 화주의 주요 지역이 포함되면 실제 집하 가능성이 높다.
        if (serviceRegions.any { it.regionMatches(vendor.mainRegion) }) {
            score += SERVICE_REGION_MATCH_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.SERVICE_REGION_MATCH)
        }

        // 주 담당 지역이 같은 경우는 같은 생활권으로 보고 추가 점수를 준다.
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

    private fun Vendor.toRecommendation(
        agency: Agency,
        previousVendorRank: Map<UUID, Int>,
    ): VendorRecommendationResult? {
        val reasons = mutableListOf<RecommendationReasonResult>()
        var score = 0

        // 대리점 입장에서도 과거 계약 화주는 다시 영업할 가치가 높은 후보로 본다.
        if (id in previousVendorRank) {
            score += PREVIOUS_CONTRACT_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.PREVIOUS_CONTRACT)
        }

        // 대리점 담당 가능 지역과 화주 주요 지역이 맞으면 영업/집하 가능성이 높다.
        if (agency.serviceRegions.any { it.regionMatches(mainRegion) }) {
            score += SERVICE_REGION_MATCH_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.SERVICE_REGION_MATCH)
        }

        // 대리점 주 담당 지역과 화주 주요 지역이 같으면 같은 권역 후보로 본다.
        if (agency.mainRegion.regionMatches(mainRegion)) {
            score += MAIN_REGION_MATCH_SCORE
            reasons += RecommendationReasonResult(RecommendationReasonType.MAIN_REGION_MATCH)
        }

        if (score == 0) {
            return null
        }

        return VendorRecommendationResult.from(
            vendor = this,
            score = score,
            reasons = reasons,
        )
    }

    private fun String.regionMatches(other: String): Boolean {
        val source = trim()
        val target = other.trim()

        // 주소 정규화/좌표 기반 거리 계산 전 단계의 단순 지역 매칭이다.
        // 운영 정책이 정해지면 시군구/동 단위 파싱 또는 좌표 거리 계산으로 교체할 수 있다.
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

    private fun findAgencyUser(userId: UUID): User {
        val user = userRepository.findById(userId)
            ?: throw GlobalException(RecommendationErrorCode.USER_NOT_FOUND)
        if (user.role != UserRole.AGENCY) {
            throw GlobalException(RecommendationErrorCode.USER_IS_NOT_AGENCY)
        }

        return user
    }

    private fun findVendorByUserId(userId: UUID): Vendor {
        return vendorRepository.findByUserId(userId)
            ?: throw GlobalException(RecommendationErrorCode.VENDOR_NOT_FOUND)
    }

    private fun findAgencyByUserId(userId: UUID): Agency {
        return agencyRepository.findByUserId(userId)
            ?: throw GlobalException(RecommendationErrorCode.AGENCY_NOT_FOUND)
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
