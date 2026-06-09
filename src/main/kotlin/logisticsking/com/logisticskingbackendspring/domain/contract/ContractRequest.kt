package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

class ContractRequest private constructor(
    // 계약 요청 식별자.
    val id: UUID,
    // 계약 요청을 등록한 화주 식별자.
    val vendorId: UUID,
    // 화주가 미리 등록한 배송 품목 식별자. 직접 입력 요청이면 null.
    val productId: UUID?,
    // 대리점 매칭에 사용할 픽업 가능 지역.
    val pickupRegion: String,
    // 실제 집하가 필요한 상세 주소.
    val pickupAddress: String?,
    // 화주가 한 달에 보낼 것으로 예상하는 박스 수.
    val monthlyVolume: Int,
    // 대리점이 배송 조건과 단가를 판단할 품목 카테고리.
    val productCategory: ProductCategory,
    // 화주가 보내려는 대표 품목명.
    val productName: String,
    // @Todo : 박스 사이즈가 규격화 돼 있다면 Enum으로 수정할 것.
    // 대리점 단가 산정에 사용할 주요 박스 규격.
    val boxSize: String,
    // 화주가 원하는 집하 시작 시간.
    val pickupStartTime: String,
    // 화주가 원하는 집하 종료 시간.
    val pickupEndTime: String,
    // 토요일 배송 조건이 필요한지 여부.
    val saturdayDeliveryRequired: Boolean,
    // 반품 회수 조건이 필요한지 여부.
    val returnRequired: Boolean,
    // 냉장/냉동 배송 조건이 필요한지 여부.
    val coldChainRequired: Boolean,
    // 화주가 기대하는 건당 희망 단가. 확정 운임은 아님.
    val targetUnitPrice: BigDecimal?,
    // 대리점이 제안할 때 참고할 추가 요청 사항.
    val memo: String?,
    // 계약 요청 진행 상태.
    val status: ContractRequestStatus,
) {

    fun update(
        productId: UUID?,
        pickupRegion: String,
        pickupAddress: String?,
        monthlyVolume: Int,
        productCategory: ProductCategory,
        productName: String,
        boxSize: String,
        pickupStartTime: String,
        pickupEndTime: String,
        saturdayDeliveryRequired: Boolean,
        returnRequired: Boolean,
        coldChainRequired: Boolean,
        targetUnitPrice: BigDecimal?,
        memo: String?,
    ): ContractRequest {
        requireDomain(
            status != ContractRequestStatus.CANCELED,
            ContractRequestErrorCode.CANCELED_REQUEST_CANNOT_BE_UPDATED,
        )
        requireDomain(
            status != ContractRequestStatus.CONTRACTED,
            ContractRequestErrorCode.CONTRACTED_REQUEST_CANNOT_BE_UPDATED,
        )

        return create(
            id = id,
            vendorId = vendorId,
            productId = productId,
            pickupRegion = pickupRegion,
            pickupAddress = pickupAddress,
            monthlyVolume = monthlyVolume,
            productCategory = productCategory,
            productName = productName,
            boxSize = boxSize,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryRequired = saturdayDeliveryRequired,
            returnRequired = returnRequired,
            coldChainRequired = coldChainRequired,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
            status = status,
        )
    }

    fun cancel(): ContractRequest {
        requireDomain(
            status != ContractRequestStatus.CONTRACTED,
            ContractRequestErrorCode.CONTRACTED_REQUEST_CANNOT_BE_CANCELED,
        )

        return restore(
            id = id,
            vendorId = vendorId,
            productId = productId,
            pickupRegion = pickupRegion,
            pickupAddress = pickupAddress,
            monthlyVolume = monthlyVolume,
            productCategory = productCategory,
            productName = productName,
            boxSize = boxSize,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryRequired = saturdayDeliveryRequired,
            returnRequired = returnRequired,
            coldChainRequired = coldChainRequired,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
            status = ContractRequestStatus.CANCELED,
        )
    }

    fun contract(): ContractRequest {
        requireDomain(
            status == ContractRequestStatus.OPEN,
            ContractRequestErrorCode.ONLY_OPEN_REQUEST_CAN_BE_CONTRACTED,
        )

        return restore(
            id = id,
            vendorId = vendorId,
            productId = productId,
            pickupRegion = pickupRegion,
            pickupAddress = pickupAddress,
            monthlyVolume = monthlyVolume,
            productCategory = productCategory,
            productName = productName,
            boxSize = boxSize,
            pickupStartTime = pickupStartTime,
            pickupEndTime = pickupEndTime,
            saturdayDeliveryRequired = saturdayDeliveryRequired,
            returnRequired = returnRequired,
            coldChainRequired = coldChainRequired,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
            status = ContractRequestStatus.CONTRACTED,
        )
    }

    companion object {
        fun create(
            id: UUID,
            vendorId: UUID,
            productId: UUID?,
            pickupRegion: String,
            pickupAddress: String?,
            monthlyVolume: Int,
            productCategory: ProductCategory,
            productName: String,
            boxSize: String,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryRequired: Boolean,
            returnRequired: Boolean,
            coldChainRequired: Boolean,
            targetUnitPrice: BigDecimal?,
            memo: String?,
            status: ContractRequestStatus = ContractRequestStatus.OPEN,
        ): ContractRequest {
            requireDomain(pickupRegion.isNotBlank(), ContractRequestErrorCode.INVALID_PICKUP_REGION)
            requireDomain(monthlyVolume > 0, ContractRequestErrorCode.INVALID_MONTHLY_VOLUME)
            requireDomain(productName.isNotBlank(), ContractRequestErrorCode.INVALID_PRODUCT_NAME)
            requireDomain(boxSize.isNotBlank(), ContractRequestErrorCode.INVALID_BOX_SIZE)
            requireDomain(
                pickupStartTime.isNotBlank() && pickupEndTime.isNotBlank(),
                ContractRequestErrorCode.INVALID_PICKUP_TIME,
            )
            requireDomain(
                targetUnitPrice == null || targetUnitPrice >= BigDecimal.ZERO,
                ContractRequestErrorCode.INVALID_TARGET_UNIT_PRICE,
            )

            return ContractRequest(
                id = id,
                vendorId = vendorId,
                productId = productId,
                pickupRegion = pickupRegion.trim(),
                pickupAddress = pickupAddress?.trim()?.takeIf { it.isNotBlank() },
                monthlyVolume = monthlyVolume,
                productCategory = productCategory,
                productName = productName.trim(),
                boxSize = boxSize.trim(),
                pickupStartTime = pickupStartTime.trim(),
                pickupEndTime = pickupEndTime.trim(),
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainRequired = coldChainRequired,
                targetUnitPrice = targetUnitPrice,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                status = status,
            )
        }

        fun restore(
            id: UUID,
            vendorId: UUID,
            productId: UUID?,
            pickupRegion: String,
            pickupAddress: String?,
            monthlyVolume: Int,
            productCategory: ProductCategory,
            productName: String,
            boxSize: String,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryRequired: Boolean,
            returnRequired: Boolean,
            coldChainRequired: Boolean,
            targetUnitPrice: BigDecimal?,
            memo: String?,
            status: ContractRequestStatus,
        ): ContractRequest {
            return ContractRequest(
                id = id,
                vendorId = vendorId,
                productId = productId,
                pickupRegion = pickupRegion,
                pickupAddress = pickupAddress,
                monthlyVolume = monthlyVolume,
                productCategory = productCategory,
                productName = productName,
                boxSize = boxSize,
                pickupStartTime = pickupStartTime,
                pickupEndTime = pickupEndTime,
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainRequired = coldChainRequired,
                targetUnitPrice = targetUnitPrice,
                memo = memo,
                status = status,
            )
        }
    }
}
