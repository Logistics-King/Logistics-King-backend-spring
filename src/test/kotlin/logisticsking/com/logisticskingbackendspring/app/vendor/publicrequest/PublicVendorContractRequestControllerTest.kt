package logisticsking.com.logisticskingbackendspring.app.vendor.publicrequest

import logisticsking.com.logisticskingbackendspring.app.permission.EndpointAccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class PublicVendorContractRequestControllerTest {

    @Test
    fun `공개 계약 요청 요약은 인증 상태와 무관하게 지역 기준 수치를 조회한다`() {
        // Given: 지역별 공개 요청 수와 최근 7일 신규 요청 수를 반환하는 Vendor 유스케이스를 준비한다.
        val controller = PublicVendorContractRequestController(
            getPublicVendorContractRequestsUseCase = object : GetPublicVendorContractRequestsUseCase {
                override fun getPublicVendorContractRequests(
                    region: String?,
                    pageable: org.springframework.data.domain.Pageable,
                ) = PageImpl(emptyList<PublicVendorContractRequestResult>(), pageable, 0)
            },
            getPublicVendorContractRequestSummaryUseCase = object : GetPublicVendorContractRequestSummaryUseCase {
                override fun getPublicVendorContractRequestSummary(region: String?) =
                    PublicVendorContractRequestSummaryResult(
                        openContractRequestCount = 8,
                        newContractRequestCount = 3,
                    )
            },
        )

        // When: 비로그인, 게스트, 로그인 사용자가 공통으로 호출할 공개 요약 API를 호출한다.
        val response = controller.getSummary("경기도 안산시")
        val endpointAccess = PublicVendorContractRequestController::class.java
            .getDeclaredMethod("getSummary", String::class.java)
            .getAnnotation(EndpointAccess::class.java)

        // Then: publicAccess가 활성화되어야 하고 Vendor 공개 요청 수만 반환해야 한다.
        assertTrue(endpointAccess.publicAccess)
        assertEquals(8, response.payload.response?.openContractRequestCount)
        assertEquals(3, response.payload.response?.newContractRequestCount)
    }
}
