package logisticsking.com.logisticskingbackendspring.domain.contract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class RecentPublicContractServiceTest {

    @Test
    fun `최근 성사 계약 조회는 서버 시각 기준 72시간 전과 정규화한 지역을 전달한다`() {
        // Given: 서버 시각을 고정하고, Contract 저장소가 전달받은 공개 조회 조건을 기록하도록 준비한다.
        val repository = RecordingContractRepository()
        val service = RecentPublicContractService(
            contractRepository = repository,
            clock = Clock.fixed(
                LocalDateTime.of(2026, 9, 23, 12, 0).atZone(ZoneId.of("Asia/Seoul")).toInstant(),
                ZoneId.of("Asia/Seoul"),
            ),
        )

        // When: 공백이 포함된 지역으로 최근 성사 계약을 조회한다.
        service.getRecentPublicContracts(" 경기도 안산시 ")

        // Then: 최근 계약의 시작 시각은 정확히 72시간 전이고, 최대 6건과 정규화한 지역이 전달되어야 한다.
        assertEquals(LocalDateTime.of(2026, 9, 20, 12, 0), repository.createdSince)
        assertEquals("경기도 안산시", repository.region)
        assertEquals(6, repository.limit)
    }

    private class RecordingContractRepository : ContractRepository {
        var region: String? = null
        var createdSince: LocalDateTime? = null
        var limit: Int? = null

        override fun save(contract: Contract): Contract = error("not used")
        override fun findById(id: UUID): Contract? = error("not used")
        override fun findAllByVendorId(vendorId: UUID, pageable: Pageable): Page<Contract> = error("not used")
        override fun findAllByAgencyId(agencyId: UUID, pageable: Pageable): Page<Contract> = error("not used")
        override fun findRecentAgencyIdsByVendorId(vendorId: UUID, limit: Int): List<UUID> = error("not used")
        override fun findRecentVendorIdsByAgencyId(agencyId: UUID, limit: Int): List<UUID> = error("not used")
        override fun existsByContractRequestId(contractRequestId: UUID): Boolean = error("not used")

        override fun findRecentPublicContracts(
            region: String?,
            createdSince: LocalDateTime,
            limit: Int,
        ): List<RecentPublicContract> {
            this.region = region
            this.createdSince = createdSince
            this.limit = limit
            return emptyList()
        }
    }
}
