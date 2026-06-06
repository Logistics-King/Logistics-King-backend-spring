package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ContractRequestTest {

    @Test
    fun `create 성공 시 계약 요청 정보를 정리하고 OPEN 상태로 생성한다`() {
        val contractRequestId = UUID.randomUUID()
        val vendorId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        val contractRequest = contractRequest(
            id = contractRequestId,
            vendorId = vendorId,
            productId = productId,
            pickupRegion = " 경기도 안산시 일동 ",
            pickupAddress = " ",
            productName = " 일반 의류 ",
            boxSize = " 60 ",
            memo = " 오전 픽업 선호 ",
        )

        assertEquals(contractRequestId, contractRequest.id)
        assertEquals(vendorId, contractRequest.vendorId)
        assertEquals(productId, contractRequest.productId)
        assertEquals("경기도 안산시 일동", contractRequest.pickupRegion)
        assertNull(contractRequest.pickupAddress)
        assertEquals("일반 의류", contractRequest.productName)
        assertEquals("60", contractRequest.boxSize)
        assertEquals("오전 픽업 선호", contractRequest.memo)
        assertEquals(ContractRequestStatus.OPEN, contractRequest.status)
    }

    @Test
    fun `update 성공 시 기존 식별자를 유지하고 정보를 변경한다`() {
        val contractRequest = contractRequest()
        val productId = UUID.randomUUID()

        val updated = contractRequest.update(
            productId = productId,
            pickupRegion = "경기도 안산시 본오동",
            pickupAddress = "경기도 안산시 상록구 본오동 202호",
            monthlyVolume = 1000,
            productCategory = ProductCategory.CLOTHING,
            productName = "여성 의류",
            boxSize = "80",
            pickupStartTime = "10:00",
            pickupEndTime = "17:00",
            saturdayDeliveryRequired = false,
            returnRequired = true,
            coldChainRequired = false,
            targetUnitPrice = BigDecimal("2100"),
            memo = "반품 회수 필요",
        )

        assertEquals(contractRequest.id, updated.id)
        assertEquals(contractRequest.vendorId, updated.vendorId)
        assertEquals(productId, updated.productId)
        assertEquals("경기도 안산시 본오동", updated.pickupRegion)
        assertEquals(1000, updated.monthlyVolume)
        assertEquals("80", updated.boxSize)
        assertEquals(BigDecimal("2100"), updated.targetUnitPrice)
    }

    @Test
    fun `cancel 성공 시 CANCELED 상태로 변경한다`() {
        val contractRequest = contractRequest()

        val canceled = contractRequest.cancel()

        assertEquals(contractRequest.id, canceled.id)
        assertEquals(ContractRequestStatus.CANCELED, canceled.status)
    }

    @Test
    fun `create 시 월 예상 물량이 1보다 작으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            contractRequest(monthlyVolume = 0)
        }

        assertEquals(ContractRequestErrorCode.INVALID_MONTHLY_VOLUME, exception.errorCode)
    }

    @Test
    fun `create 시 픽업 지역이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            contractRequest(pickupRegion = " ")
        }

        assertEquals(ContractRequestErrorCode.INVALID_PICKUP_REGION, exception.errorCode)
    }

    @Test
    fun `create 시 희망 단가가 음수면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            contractRequest(targetUnitPrice = BigDecimal("-1"))
        }

        assertEquals(ContractRequestErrorCode.INVALID_TARGET_UNIT_PRICE, exception.errorCode)
    }

    @Test
    fun `취소된 계약 요청은 수정할 수 없다`() {
        val canceled = contractRequest().cancel()

        val exception = assertThrows(GlobalException::class.java) {
            canceled.update(
                productId = null,
                pickupRegion = "경기도 안산시 본오동",
                pickupAddress = null,
                monthlyVolume = 1000,
                productCategory = ProductCategory.CLOTHING,
                productName = "여성 의류",
                boxSize = "80",
                pickupStartTime = "10:00",
                pickupEndTime = "17:00",
                saturdayDeliveryRequired = false,
                returnRequired = true,
                coldChainRequired = false,
                targetUnitPrice = BigDecimal("2100"),
                memo = null,
            )
        }

        assertEquals(ContractRequestErrorCode.CANCELED_REQUEST_CANNOT_BE_UPDATED, exception.errorCode)
    }

    private fun contractRequest(
        id: UUID = UUID.randomUUID(),
        vendorId: UUID = UUID.randomUUID(),
        productId: UUID? = null,
        pickupRegion: String = "경기도 안산시 일동",
        pickupAddress: String? = "경기도 안산시 상록구 일동 101호",
        monthlyVolume: Int = 800,
        productName: String = "일반 의류",
        boxSize: String = "60",
        targetUnitPrice: BigDecimal? = BigDecimal("2000"),
        memo: String? = "의류 중심",
    ): ContractRequest {
        return ContractRequest.create(
            id = id,
            vendorId = vendorId,
            productId = productId,
            pickupRegion = pickupRegion,
            pickupAddress = pickupAddress,
            monthlyVolume = monthlyVolume,
            productCategory = ProductCategory.CLOTHING,
            productName = productName,
            boxSize = boxSize,
            pickupStartTime = "09:00",
            pickupEndTime = "18:00",
            saturdayDeliveryRequired = true,
            returnRequired = true,
            coldChainRequired = false,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
        )
    }
}
