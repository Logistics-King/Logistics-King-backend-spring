package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
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
            boxSize = BoxSize.SIZE_60,
            memo = " 오전 픽업 선호 ",
        )

        assertEquals(contractRequestId, contractRequest.id)
        assertEquals(vendorId, contractRequest.vendorId)
        assertEquals(productId, contractRequest.productId)
        assertEquals("경기도 안산시 일동", contractRequest.pickupRegion)
        assertNull(contractRequest.pickupAddress)
        assertEquals("일반 의류", contractRequest.productName)
        assertEquals(BoxSize.SIZE_60, contractRequest.boxSize)
        assertEquals("오전 픽업 선호", contractRequest.memo)
        assertEquals(ContractRequestStatus.OPEN, contractRequest.status)
    }

    @Test
    fun `create 성공 시 여러 배송 물품 라인을 가진 계약 요청을 생성한다`() {
        val contractRequest = contractRequest(
            monthlyVolume = 12,
            boxSize = BoxSize.SIZE_60,
        )

        val frozenItem = contractRequestItem(
            productName = "냉동 식품",
            boxSize = BoxSize.SIZE_100,
            boxQuantity = 4,
            coldChainType = ColdChainType.FROZEN,
        )
        val updated = contractRequest.update(
            productId = null,
            pickupRegion = contractRequest.pickupRegion,
            pickupAddress = contractRequest.pickupAddress,
            monthlyVolume = 12,
            productCategory = ProductCategory.CLOTHING,
            productName = "대표 품목",
            boxSize = BoxSize.SIZE_60,
            pickupStartTime = contractRequest.pickupStartTime,
            pickupEndTime = contractRequest.pickupEndTime,
            saturdayDeliveryRequired = contractRequest.saturdayDeliveryRequired,
            returnRequired = contractRequest.returnRequired,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = contractRequest.targetUnitPrice,
            memo = contractRequest.memo,
            items = listOf(contractRequestItem(boxQuantity = 8), frozenItem),
        )

        assertEquals(2, updated.items.size)
        assertEquals(BoxSize.SIZE_60, updated.items[0].boxSize)
        assertEquals(8, updated.items[0].boxQuantity)
        assertEquals(ColdChainType.FROZEN, updated.items[1].coldChainType)
        assertEquals(BoxSize.SIZE_60, updated.boxSize)
    }

    @Test
    fun `create 시 배송 물품 라인이 비어 있으면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            ContractRequest.create(
                id = UUID.randomUUID(),
                type = ContractRequestType.VENDOR_OFFER,
                requesterId = UUID.randomUUID(),
                approverId = null,
                productId = null,
                pickupRegion = "경기도 안산시 일동",
                pickupAddress = null,
                monthlyVolume = 1,
                productCategory = ProductCategory.CLOTHING,
                productName = "일반 의류",
                boxSize = BoxSize.SIZE_60,
                pickupStartTime = "09:00",
                pickupEndTime = "18:00",
                saturdayDeliveryRequired = true,
                returnRequired = true,
                coldChainType = ColdChainType.NONE,
                targetUnitPrice = null,
                memo = null,
                items = emptyList(),
            )
        }

        assertEquals(ContractRequestErrorCode.INVALID_ITEMS, exception.errorCode)
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
            boxSize = BoxSize.SIZE_80,
            pickupStartTime = "10:00",
            pickupEndTime = "17:00",
            saturdayDeliveryRequired = false,
            returnRequired = true,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = BigDecimal("2100"),
            memo = "반품 회수 필요",
            items = listOf(contractRequestItem(productId = productId, boxSize = BoxSize.SIZE_80)),
        )

        assertEquals(contractRequest.id, updated.id)
        assertEquals(contractRequest.vendorId, updated.vendorId)
        assertEquals(productId, updated.productId)
        assertEquals("경기도 안산시 본오동", updated.pickupRegion)
        assertEquals(1000, updated.monthlyVolume)
        assertEquals(BoxSize.SIZE_80, updated.boxSize)
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
    fun `contract 성공 시 CONTRACTED 상태로 변경한다`() {
        val contractRequest = contractRequest()

        val contracted = contractRequest.contract()

        assertEquals(contractRequest.id, contracted.id)
        assertEquals(ContractRequestStatus.CONTRACTED, contracted.status)
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
                boxSize = BoxSize.SIZE_80,
                pickupStartTime = "10:00",
                pickupEndTime = "17:00",
                saturdayDeliveryRequired = false,
                returnRequired = true,
                coldChainType = ColdChainType.NONE,
                targetUnitPrice = BigDecimal("2100"),
                memo = null,
                items = listOf(contractRequestItem(boxSize = BoxSize.SIZE_80)),
            )
        }

        assertEquals(ContractRequestErrorCode.CANCELED_REQUEST_CANNOT_BE_UPDATED, exception.errorCode)
    }

    @Test
    fun `계약 완료된 계약 요청은 수정할 수 없다`() {
        val contracted = contractRequest().contract()

        val exception = assertThrows(GlobalException::class.java) {
            contracted.update(
                productId = null,
                pickupRegion = "경기도 안산시 본오동",
                pickupAddress = null,
                monthlyVolume = 1000,
                productCategory = ProductCategory.CLOTHING,
                productName = "여성 의류",
                boxSize = BoxSize.SIZE_80,
                pickupStartTime = "10:00",
                pickupEndTime = "17:00",
                saturdayDeliveryRequired = false,
                returnRequired = true,
                coldChainType = ColdChainType.NONE,
                targetUnitPrice = BigDecimal("2100"),
                memo = null,
                items = listOf(contractRequestItem(boxSize = BoxSize.SIZE_80)),
            )
        }

        assertEquals(ContractRequestErrorCode.CONTRACTED_REQUEST_CANNOT_BE_UPDATED, exception.errorCode)
    }

    @Test
    fun `거절된 계약 요청은 수정할 수 없다`() {
        val rejected = contractRequest().reject()

        val exception = assertThrows(GlobalException::class.java) {
            rejected.update(
                productId = null,
                pickupRegion = "경기도 안산시 본오동",
                pickupAddress = null,
                monthlyVolume = 1000,
                productCategory = ProductCategory.CLOTHING,
                productName = "여성 의류",
                boxSize = BoxSize.SIZE_80,
                pickupStartTime = "10:00",
                pickupEndTime = "17:00",
                saturdayDeliveryRequired = false,
                returnRequired = true,
                coldChainType = ColdChainType.NONE,
                targetUnitPrice = BigDecimal("2100"),
                memo = null,
                items = listOf(contractRequestItem(boxSize = BoxSize.SIZE_80)),
            )
        }

        assertEquals(ContractRequestErrorCode.REJECTED_REQUEST_CANNOT_BE_UPDATED, exception.errorCode)
    }

    @Test
    fun `계약 완료된 계약 요청은 취소할 수 없다`() {
        val contracted = contractRequest().contract()

        val exception = assertThrows(GlobalException::class.java) {
            contracted.cancel()
        }

        assertEquals(ContractRequestErrorCode.CONTRACTED_REQUEST_CANNOT_BE_CANCELED, exception.errorCode)
    }

    @Test
    fun `거절된 계약 요청은 취소할 수 없다`() {
        val rejected = contractRequest().reject()

        val exception = assertThrows(GlobalException::class.java) {
            rejected.cancel()
        }

        assertEquals(ContractRequestErrorCode.REJECTED_REQUEST_CANNOT_BE_CANCELED, exception.errorCode)
    }

    @Test
    fun `단건 계약 요청은 회수와 배송 희망 기간을 가진다`() {
        val pickupDateFrom = LocalDate.of(2026, 6, 22)
        val pickupDateTo = LocalDate.of(2026, 6, 26)
        val deliveryDateFrom = LocalDate.of(2026, 6, 24)
        val deliveryDateTo = LocalDate.of(2026, 6, 30)

        val contractRequest = contractRequest(
            pickupDateFrom = pickupDateFrom,
            pickupDateTo = pickupDateTo,
            deliveryDateFrom = deliveryDateFrom,
            deliveryDateTo = deliveryDateTo,
        )

        assertEquals(ContractRequestContractType.SINGLE, contractRequest.contractType)
        assertEquals(pickupDateFrom, contractRequest.pickupDateFrom)
        assertEquals(pickupDateTo, contractRequest.pickupDateTo)
        assertEquals(deliveryDateFrom, contractRequest.deliveryDateFrom)
        assertEquals(deliveryDateTo, contractRequest.deliveryDateTo)
    }

    @Test
    fun `배송 희망 종료일이 회수 시작일보다 앞서면 예외가 발생한다`() {
        val exception = assertThrows(GlobalException::class.java) {
            contractRequest(
                pickupDateFrom = LocalDate.of(2026, 6, 22),
                pickupDateTo = LocalDate.of(2026, 6, 26),
                deliveryDateFrom = LocalDate.of(2026, 6, 20),
                deliveryDateTo = LocalDate.of(2026, 6, 21),
            )
        }

        assertEquals(ContractRequestErrorCode.INVALID_DELIVERY_DATE_RANGE, exception.errorCode)
    }

    @Test
    fun `정기 계약 요청은 주간 회수 요일을 가진다`() {
        val contractRequest = contractRequest(
            contractType = ContractRequestContractType.RECURRING,
            recurringPickupCycle = RecurringPickupCycle.WEEKLY,
            recurringPickupDaysOfWeek = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
        )

        assertEquals(ContractRequestContractType.RECURRING, contractRequest.contractType)
        assertEquals(RecurringPickupCycle.WEEKLY, contractRequest.recurringPickupCycle)
        assertEquals(listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), contractRequest.recurringPickupDaysOfWeek)
    }

    @Test
    fun `정기 월간 계약 요청은 회수 일자가 필요하다`() {
        val exception = assertThrows(GlobalException::class.java) {
            contractRequest(
                contractType = ContractRequestContractType.RECURRING,
                recurringPickupCycle = RecurringPickupCycle.MONTHLY,
            )
        }

        assertEquals(ContractRequestErrorCode.INVALID_RECURRING_PICKUP_RULE, exception.errorCode)
    }

    private fun contractRequest(
        id: UUID = UUID.randomUUID(),
        vendorId: UUID = UUID.randomUUID(),
        productId: UUID? = null,
        pickupRegion: String = "경기도 안산시 일동",
        pickupAddress: String? = "경기도 안산시 상록구 일동 101호",
        contractType: ContractRequestContractType = ContractRequestContractType.SINGLE,
        pickupDateFrom: LocalDate? = null,
        pickupDateTo: LocalDate? = null,
        deliveryDateFrom: LocalDate? = null,
        deliveryDateTo: LocalDate? = null,
        recurringPickupCycle: RecurringPickupCycle? = null,
        recurringPickupDaysOfWeek: List<DayOfWeek> = emptyList(),
        recurringPickupDayOfMonth: Int? = null,
        monthlyVolume: Int = 800,
        productName: String = "일반 의류",
        boxSize: BoxSize = BoxSize.SIZE_60,
        targetUnitPrice: BigDecimal? = BigDecimal("2000"),
        memo: String? = "의류 중심",
    ): ContractRequest {
        return ContractRequest.create(
            id = id,
            type = ContractRequestType.VENDOR_OFFER,
            requesterId = vendorId,
            approverId = null,
            productId = productId,
            pickupRegion = pickupRegion,
            pickupAddress = pickupAddress,
            contractType = contractType,
            pickupDateFrom = pickupDateFrom,
            pickupDateTo = pickupDateTo,
            deliveryDateFrom = deliveryDateFrom,
            deliveryDateTo = deliveryDateTo,
            recurringPickupCycle = recurringPickupCycle,
            recurringPickupDaysOfWeek = recurringPickupDaysOfWeek,
            recurringPickupDayOfMonth = recurringPickupDayOfMonth,
            monthlyVolume = monthlyVolume,
            productCategory = ProductCategory.CLOTHING,
            productName = productName,
            boxSize = boxSize,
            pickupStartTime = "09:00",
            pickupEndTime = "18:00",
            saturdayDeliveryRequired = true,
            returnRequired = true,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
            items = listOf(
                contractRequestItem(
                    productId = productId,
                    productName = productName,
                    boxSize = boxSize,
                    boxQuantity = monthlyVolume.takeIf { it > 0 } ?: 1,
                    targetUnitPrice = targetUnitPrice,
                )
            ),
        )
    }

    private fun contractRequestItem(
        productId: UUID? = null,
        productName: String = "일반 의류",
        boxSize: BoxSize = BoxSize.SIZE_60,
        boxQuantity: Int = 800,
        itemQuantity: Int = 0,
        coldChainType: ColdChainType = ColdChainType.NONE,
        targetUnitPrice: BigDecimal? = BigDecimal("2000"),
    ): ContractRequestItem {
        return ContractRequestItem.create(
            id = UUID.randomUUID(),
            productId = productId,
            productCategory = ProductCategory.CLOTHING,
            productName = productName,
            boxSize = boxSize,
            boxQuantity = boxQuantity,
            itemQuantity = itemQuantity,
            averageWeightGram = null,
            fragile = false,
            liquid = false,
            freshFood = false,
            coldChainType = coldChainType,
            targetUnitPrice = targetUnitPrice,
        )
    }
}
