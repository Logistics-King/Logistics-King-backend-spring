package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.GlobalException
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ContractTest {

    @Test
    fun `create 성공 시 계약 요청과 제안 조건을 스냅샷으로 저장한다`() {
        val contractId = UUID.randomUUID()
        val requestId = UUID.randomUUID()
        val vendorId = UUID.randomUUID()
        val agencyId = UUID.randomUUID()
        val request = contractRequest(id = requestId, vendorId = vendorId)
        val proposal = proposal(contractRequestId = requestId, vendorId = vendorId, agencyId = agencyId)
        val items = contractItems(request, proposal)

        val contract = Contract.create(
            id = contractId,
            contractRequest = request,
            proposal = proposal,
            items = items,
        )

        assertEquals(contractId, contract.id)
        assertEquals(requestId, contract.contractRequestId)
        assertEquals(proposal.id, contract.proposalId)
        assertEquals(vendorId, contract.vendorId)
        assertEquals(agencyId, contract.agencyId)
        assertEquals(request.pickupRegion, contract.pickupRegion)
        assertEquals(request.monthlyVolume, contract.monthlyVolume)
        assertEquals(request.productCategory, contract.productCategory)
        assertEquals(request.productName, contract.productName)
        assertEquals(proposal.unitPrice, contract.unitPrice)
        assertEquals(proposal.pickupStartTime, contract.pickupStartTime)
        assertEquals(proposal.saturdayDeliveryAvailable, contract.saturdayDeliveryAvailable)
        assertEquals(items, contract.items)
        assertEquals(ContractStatus.ACTIVE, contract.status)
    }

    @Test
    fun `create 시 계약 요청과 제안의 요청 또는 화주가 다르면 예외가 발생한다`() {
        val request = contractRequest(vendorId = UUID.randomUUID())
        val proposal = proposal(
            contractRequestId = UUID.randomUUID(),
            vendorId = UUID.randomUUID(),
        )

        val exception = assertThrows(GlobalException::class.java) {
            Contract.create(
                id = UUID.randomUUID(),
                contractRequest = request,
                proposal = proposal,
                items = contractItems(request, proposal),
            )
        }

        assertEquals(ContractErrorCode.INVALID_CONTRACT_REQUEST_PROPOSAL, exception.errorCode)
    }

    private fun contractRequest(
        id: UUID = UUID.randomUUID(),
        vendorId: UUID = UUID.randomUUID(),
    ): ContractRequest {
        return ContractRequest.create(
            id = id,
            type = ContractRequestType.VENDOR_OFFER,
            requesterId = vendorId,
            approverId = null,
            productId = null,
            pickupRegion = "경기도 안산시 일동",
            pickupAddress = "경기도 안산시 상록구 일동 101호",
            monthlyVolume = 800,
            productCategory = ProductCategory.CLOTHING,
            productName = "일반 의류",
            boxSize = BoxSize.SIZE_60,
            pickupStartTime = "09:00",
            pickupEndTime = "18:00",
            saturdayDeliveryRequired = true,
            returnRequired = true,
            coldChainType = ColdChainType.NONE,
            targetUnitPrice = BigDecimal("2000"),
            memo = "의류 중심",
            items = listOf(
                ContractRequestItem.create(
                    id = UUID.randomUUID(),
                    productId = null,
                    productCategory = ProductCategory.CLOTHING,
                    productName = "일반 의류",
                    boxSize = BoxSize.SIZE_60,
                    boxQuantity = 800,
                    itemQuantity = 0,
                    averageWeightGram = null,
                    fragile = false,
                    liquid = false,
                    freshFood = false,
                    coldChainType = ColdChainType.NONE,
                    targetUnitPrice = BigDecimal("2000"),
                )
            ),
        )
    }

    private fun proposal(
        id: UUID = UUID.randomUUID(),
        contractRequestId: UUID = UUID.randomUUID(),
        vendorId: UUID = UUID.randomUUID(),
        agencyId: UUID = UUID.randomUUID(),
    ): Proposal {
        return Proposal.create(
            id = id,
            contractRequestId = contractRequestId,
            vendorId = vendorId,
            agencyId = agencyId,
            unitPrice = BigDecimal("2050"),
            pickupStartTime = "10:00",
            pickupEndTime = "17:00",
            saturdayDeliveryAvailable = true,
            returnAvailable = true,
            coldChainType = ColdChainType.NONE,
            memo = "의류 800박스 기준 집하 가능",
            items = listOf(
                ProposalItem.create(
                    id = UUID.randomUUID(),
                    contractRequestItemId = UUID.randomUUID(),
                    unitPrice = BigDecimal("2050"),
                )
            ),
        )
    }

    private fun contractItems(
        request: ContractRequest,
        proposal: Proposal,
    ): List<ContractItem> {
        return request.items.map {
            ContractItem.fromRequestItem(
                id = UUID.randomUUID(),
                item = it,
                unitPrice = proposal.unitPrice,
            )
        }
    }
}
