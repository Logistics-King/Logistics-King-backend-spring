package logisticsking.com.logisticskingbackendspring.domain.contract

import logisticsking.com.logisticskingbackendspring.domain.common.BoxSize
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType
import logisticsking.com.logisticskingbackendspring.domain.error.requireDomain
import logisticsking.com.logisticskingbackendspring.domain.vendor.ProductCategory
import java.math.BigDecimal
import java.util.UUID

class ContractRequest private constructor(
    // 계약 요청 식별자.
    val id: UUID,
    // 계약 요청 타입. VENDOR_OFFER는 화주 -> 대리점, AGENCY_OFFER는 대리점 -> 화주.
    val type: ContractRequestType,
    // 계약 요청을 시작한 주체 타입.
    val requesterType: ContractPartyType,
    // 계약 요청을 시작한 주체 식별자.
    val requesterId: UUID,
    // 계약 요청을 승인할 주체 타입.
    val approverType: ContractPartyType,
    // 특정 승인자가 정해진 경우의 식별자. 공개 요청이면 null.
    val approverId: UUID?,
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
    // 대리점 단가 산정에 사용할 주요 박스 규격.
    val boxSize: BoxSize,
    // 화주가 원하는 집하 시작 시간.
    val pickupStartTime: String,
    // 화주가 원하는 집하 종료 시간.
    val pickupEndTime: String,
    // 토요일 배송 조건이 필요한지 여부.
    val saturdayDeliveryRequired: Boolean,
    // 반품 회수 조건이 필요한지 여부.
    val returnRequired: Boolean,
    // 필요한 콜드체인 조건. NONE, REFRIGERATED, FROZEN 중 하나.
    val coldChainType: ColdChainType,
    // 화주가 기대하는 건당 희망 단가. 확정 운임은 아님.
    val targetUnitPrice: BigDecimal?,
    // 대리점이 제안할 때 참고할 추가 요청 사항.
    val memo: String?,
    // 계약 요청 진행 상태.
    val status: ContractRequestStatus,
) {
    val vendorId: UUID
        get() = when (ContractPartyType.VENDOR) {
            requesterType -> requesterId
            approverType -> requireNotNull(approverId) { "화주 승인자 ID가 필요합니다." }
            else -> error("계약 요청에 화주가 없습니다.")
        }

    val agencyId: UUID?
        get() = when (ContractPartyType.AGENCY) {
            requesterType -> requesterId
            approverType -> approverId
            else -> null
        }

    fun isRequester(
        partyType: ContractPartyType,
        partyId: UUID,
    ): Boolean {
        return requesterType == partyType && requesterId == partyId
    }

    fun isParticipant(
        partyType: ContractPartyType,
        partyId: UUID,
    ): Boolean {
        return isRequester(partyType, partyId) ||
            (approverType == partyType && (approverId == null || approverId == partyId))
    }

    fun update(
        productId: UUID?,
        pickupRegion: String,
        pickupAddress: String?,
        monthlyVolume: Int,
        productCategory: ProductCategory,
        productName: String,
        boxSize: BoxSize,
        pickupStartTime: String,
        pickupEndTime: String,
        saturdayDeliveryRequired: Boolean,
        returnRequired: Boolean,
        coldChainType: ColdChainType,
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
        requireDomain(
            status != ContractRequestStatus.REJECTED,
            ContractRequestErrorCode.REJECTED_REQUEST_CANNOT_BE_UPDATED,
        )

        return create(
            id = id,
            type = type,
            requesterId = requesterId,
            approverId = approverId,
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
            coldChainType = coldChainType,
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
        requireDomain(
            status != ContractRequestStatus.REJECTED,
            ContractRequestErrorCode.REJECTED_REQUEST_CANNOT_BE_CANCELED,
        )

        return restore(
            id = id,
            type = type,
            requesterType = requesterType,
            requesterId = requesterId,
            approverType = approverType,
            approverId = approverId,
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
            coldChainType = coldChainType,
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
            type = type,
            requesterType = requesterType,
            requesterId = requesterId,
            approverType = approverType,
            approverId = approverId,
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
            coldChainType = coldChainType,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
            status = ContractRequestStatus.CONTRACTED,
        )
    }

    fun reject(): ContractRequest {
        requireDomain(
            status == ContractRequestStatus.OPEN,
            ContractRequestErrorCode.ONLY_OPEN_REQUEST_CAN_BE_REJECTED,
        )

        return restore(
            id = id,
            type = type,
            requesterType = requesterType,
            requesterId = requesterId,
            approverType = approverType,
            approverId = approverId,
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
            coldChainType = coldChainType,
            targetUnitPrice = targetUnitPrice,
            memo = memo,
            status = ContractRequestStatus.REJECTED,
        )
    }

    companion object {
        fun create(
            id: UUID,
            type: ContractRequestType,
            requesterId: UUID,
            approverId: UUID?,
            productId: UUID?,
            pickupRegion: String,
            pickupAddress: String?,
            monthlyVolume: Int,
            productCategory: ProductCategory,
            productName: String,
            boxSize: BoxSize,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryRequired: Boolean,
            returnRequired: Boolean,
            coldChainType: ColdChainType,
            targetUnitPrice: BigDecimal?,
            memo: String?,
            status: ContractRequestStatus = ContractRequestStatus.OPEN,
        ): ContractRequest {
            requireDomain(
                approverId != requesterId,
                ContractRequestErrorCode.INVALID_CONTRACT_PARTY,
            )
            requireDomain(pickupRegion.isNotBlank(), ContractRequestErrorCode.INVALID_PICKUP_REGION)
            requireDomain(monthlyVolume > 0, ContractRequestErrorCode.INVALID_MONTHLY_VOLUME)
            requireDomain(productName.isNotBlank(), ContractRequestErrorCode.INVALID_PRODUCT_NAME)
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
                type = type,
                requesterType = type.requesterType,
                requesterId = requesterId,
                approverType = type.approverType,
                approverId = approverId,
                productId = productId,
                pickupRegion = pickupRegion.trim(),
                pickupAddress = pickupAddress?.trim()?.takeIf { it.isNotBlank() },
                monthlyVolume = monthlyVolume,
                productCategory = productCategory,
                productName = productName.trim(),
                boxSize = boxSize,
                pickupStartTime = pickupStartTime.trim(),
                pickupEndTime = pickupEndTime.trim(),
                saturdayDeliveryRequired = saturdayDeliveryRequired,
                returnRequired = returnRequired,
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
                memo = memo?.trim()?.takeIf { it.isNotBlank() },
                status = status,
            )
        }

        fun restore(
            id: UUID,
            type: ContractRequestType,
            requesterType: ContractPartyType,
            requesterId: UUID,
            approverType: ContractPartyType,
            approverId: UUID?,
            productId: UUID?,
            pickupRegion: String,
            pickupAddress: String?,
            monthlyVolume: Int,
            productCategory: ProductCategory,
            productName: String,
            boxSize: BoxSize,
            pickupStartTime: String,
            pickupEndTime: String,
            saturdayDeliveryRequired: Boolean,
            returnRequired: Boolean,
            coldChainType: ColdChainType,
            targetUnitPrice: BigDecimal?,
            memo: String?,
            status: ContractRequestStatus,
        ): ContractRequest {
            requireDomain(
                requesterType == type.requesterType && approverType == type.approverType,
                ContractRequestErrorCode.INVALID_CONTRACT_PARTY,
            )

            return ContractRequest(
                id = id,
                type = type,
                requesterType = requesterType,
                requesterId = requesterId,
                approverType = approverType,
                approverId = approverId,
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
                coldChainType = coldChainType,
                targetUnitPrice = targetUnitPrice,
                memo = memo,
                status = status,
            )
        }
    }
}
